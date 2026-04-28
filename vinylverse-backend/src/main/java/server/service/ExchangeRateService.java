package server.service;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ExchangeRateService {

    private static final Logger log = LoggerFactory.getLogger(ExchangeRateService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${exchange-rate.rsd-api-url:https://open.er-api.com/v6/latest/RSD}")
    private String rsdApiUrl;

    @Value("${exchange-rate.cache-minutes:720}")
    private long cacheMinutes;

    @Value("${paypal.rsd-to-payment-rate:0.0085}")
    private BigDecimal fallbackRsdToPaymentRate;

    private String cachedCurrency;
    private BigDecimal cachedRate;
    private Instant cachedUntil = Instant.EPOCH;

    public ExchangeRateService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder.build();
        this.objectMapper = objectMapper;
    }

    public synchronized BigDecimal rsdTo(String targetCurrency) {
        String currency = normalizeCurrency(targetCurrency);
        if ("RSD".equals(currency)) {
            return BigDecimal.ONE;
        }

        Instant now = Instant.now();
        if (currency.equals(cachedCurrency) && cachedRate != null && now.isBefore(cachedUntil)) {
            return cachedRate;
        }

        try {
            BigDecimal liveRate = fetchLiveRsdRate(currency);
            cachedCurrency = currency;
            cachedRate = liveRate;
            cachedUntil = now.plus(Duration.ofMinutes(Math.max(cacheMinutes, 1)));
            return liveRate;
        } catch (Exception ex) {
            log.warn("Ne mogu da preuzmem live kurs RSD -> {}. Koristim fallback kurs {}.",
                    currency, fallbackRsdToPaymentRate, ex);
            return fallbackRate();
        }
    }

    private BigDecimal fetchLiveRsdRate(String currency) throws Exception {
        String rawUrl = rsdApiUrl == null || rsdApiUrl.isBlank()
                ? "https://open.er-api.com/v6/latest/RSD"
                : rsdApiUrl;
        String url = rawUrl.replace("{currency}", currency);
        URI uri = Objects.requireNonNull(URI.create(url));
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        String body = response.getBody();
        if (body == null || body.isBlank()) {
            throw new IllegalStateException("API je vratio prazan odgovor");
        }
        JsonNode root = objectMapper.readTree(body);
        JsonNode rateNode = root.path("rates").path(currency);
        if (rateNode.isMissingNode() || !rateNode.isNumber()) {
            throw new IllegalStateException("API nije vratio kurs za " + currency);
        }

        BigDecimal rate = rateNode.decimalValue();
        if (rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("API je vratio neispravan kurs za " + currency + ": " + rate);
        }
        return rate;
    }

    private BigDecimal fallbackRate() {
        if (fallbackRsdToPaymentRate == null || fallbackRsdToPaymentRate.compareTo(BigDecimal.ZERO) <= 0) {
            return new BigDecimal("0.0085");
        }
        return fallbackRsdToPaymentRate;
    }

    private static String normalizeCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            return "EUR";
        }
        return currency.trim().toUpperCase();
    }
}

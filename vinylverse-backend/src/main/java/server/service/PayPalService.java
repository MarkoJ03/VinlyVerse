package server.service;

import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import server.DTOs.PayPalCreateOrderResponseDTO;
import server.exception.BadRequestException;

@Service
public class PayPalService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${paypal.client-id:}")
    private String clientId;

    @Value("${paypal.client-secret:}")
    private String clientSecret;

    @Value("${paypal.base-url:https://api-m.sandbox.paypal.com}")
    private String baseUrl;

    @Value("${paypal.return-url:http://localhost:4200/checkout/success}")
    private String returnUrl;

    @Value("${paypal.cancel-url:http://localhost:4200/checkout/cancel}")
    private String cancelUrl;

    public PayPalService(RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        this.restTemplate = restTemplateBuilder.build();
        this.objectMapper = objectMapper;
    }

    public PayPalCreateOrderResponseDTO createOrder(BigDecimal amount, String currency) {
        return createOrder(amount, currency, returnUrl, cancelUrl);
    }

    /**
     * @param returnUrl  pun URL (npr. sa ?orderId= da frontend učita porudžbinu nakon odobrenja)
     * @param cancelUrl  pun URL
     */
    public PayPalCreateOrderResponseDTO createOrder(BigDecimal amount, String currency, String returnUrl, String cancelUrl) {
        validateCredentials();
        String accessToken = Objects.requireNonNull(fetchAccessToken(), "PayPal access token je null");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String safeReturn = escapeJsonString(Objects.requireNonNull(returnUrl, "returnUrl"));
        String safeCancel = escapeJsonString(Objects.requireNonNull(cancelUrl, "cancelUrl"));

        String payload = """
                {
                  "intent": "CAPTURE",
                  "purchase_units": [
                    {
                      "amount": {
                        "currency_code": "%s",
                        "value": "%s"
                      }
                    }
                  ],
                  "application_context": {
                    "return_url": "%s",
                    "cancel_url": "%s",
                    "user_action": "PAY_NOW"
                  }
                }
                """.formatted(currency, amount.toPlainString(), safeReturn, safeCancel);

        HttpEntity<String> request = new HttpEntity<>(payload, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/v2/checkout/orders", request, String.class);
        JsonNode body = parseBody(response.getBody());
        JsonNode idNode = body.get("id");
        if (idNode == null || idNode.isNull()) {
            throw new BadRequestException("PayPal create-order response je neispravan");
        }

        String paypalOrderId = idNode.asText();
        String status = body.path("status").asText("UNKNOWN");
        String approveUrl = extractApproveUrl(body);
        return new PayPalCreateOrderResponseDTO(paypalOrderId, approveUrl, status);
    }

    public String captureOrder(String paypalOrderId) {
        validateCredentials();
        String accessToken = Objects.requireNonNull(fetchAccessToken(), "PayPal access token je null");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/v2/checkout/orders/" + paypalOrderId + "/capture",
                request,
                String.class);

        JsonNode body = parseBody(response.getBody());
        JsonNode statusNode = body.get("status");
        if (statusNode == null || statusNode.isNull()) {
            throw new BadRequestException("PayPal capture response je neispravan");
        }
        return statusNode.asText();
    }

    private String fetchAccessToken() {
        String safeClientId = Objects.requireNonNull(clientId, "paypal.client-id nije postavljen");
        String safeClientSecret = Objects.requireNonNull(clientSecret, "paypal.client-secret nije postavljen");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(safeClientId, safeClientSecret);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>("grant_type=client_credentials", headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/v1/oauth2/token", request, String.class);
        JsonNode body = parseBody(response.getBody());
        JsonNode tokenNode = body.get("access_token");
        if (tokenNode == null || tokenNode.isNull()) {
            throw new BadRequestException("Nije moguce preuzeti PayPal access token");
        }
        return tokenNode.asText();
    }

    private String extractApproveUrl(JsonNode body) {
        JsonNode links = body.get("links");
        if (links == null || !links.isArray()) {
            return null;
        }
        for (JsonNode link : links) {
            if ("approve".equals(link.path("rel").asText())) {
                return link.path("href").asText(null);
            }
        }
        return null;
    }

    private JsonNode parseBody(String rawBody) {
        try {
            return objectMapper.readTree(rawBody);
        } catch (Exception ex) {
            throw new BadRequestException("Neispravan PayPal odgovor");
        }
    }

    private void validateCredentials() {
        if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
            throw new BadRequestException("PayPal kredencijali nisu podeseni u application properties");
        }
    }

    private static String escapeJsonString(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}

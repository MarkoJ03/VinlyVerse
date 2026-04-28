package server.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import server.DTOs.NarudzbinaDTO;
import server.DTOs.PayPalCaptureResponseDTO;
import server.DTOs.PayPalCreateOrderResponseDTO;
import server.DTOs.StavkaNarudzbineDTO;
import server.exception.BadRequestException;
import server.model.Korisnik;
import server.model.NacinPlacanja;
import server.model.Narudzbina;
import server.model.PaymentProvider;
import server.model.Ploca;
import server.model.StatusNarudzbine;
import server.model.StavkaNarudzbine;
import server.repository.KorisnikRepository;
import server.repository.NarudzbinaRepository;
import server.repository.PlocaRepository;

@Service
public class NarudzbinaService extends BaseService<Narudzbina, NarudzbinaDTO, Long> {

    private static final Pattern EMAIL_LABAV = Pattern
            .compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    @Autowired
    private NarudzbinaRepository narudzbinaRepository;

    @Autowired
    private KorisnikRepository korisnikRepository;

    @Autowired
    private PlocaRepository plocaRepository;

    @Autowired
    private PayPalService payPalService;

    @Autowired
    private RacunEmailService racunEmailService;

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Value("${paypal.return-url:http://localhost:4200/checkout/success}")
    private String paypalReturnUrl;

    @Value("${paypal.cancel-url:http://localhost:4200/checkout/cancel}")
    private String paypalCancelUrl;

    @Value("${paypal.payment-currency:EUR}")
    private String paypalPaymentCurrency;

    @Override
    protected CrudRepository<Narudzbina, Long> getRepository() {
        return narudzbinaRepository;
    }

    @Override
    protected NarudzbinaDTO convertToDTO(Narudzbina entity) {
        List<StavkaNarudzbineDTO> stavkeDto = new ArrayList<>();
        if (entity.getStavke() != null) {
            for (StavkaNarudzbine stavka : entity.getStavke()) {
                String nazivPloce = stavka.getPloca() != null && stavka.getPloca().getProizvod() != null
                        ? stavka.getPloca().getProizvod().getNaziv()
                        : null;
                stavkeDto.add(new StavkaNarudzbineDTO(
                        stavka.getId(),
                        entity.getId(),
                        stavka.getPloca() != null ? stavka.getPloca().getId() : null,
                        nazivPloce,
                        stavka.getJedinicnaCena(),
                        stavka.getUkupno(),
                        stavka.getVidljiv()));
            }
        }

        return new NarudzbinaDTO(
                entity.getId(),
                entity.getKorisnik() != null ? entity.getKorisnik().getId() : null,
                entity.getKorisnik() != null ? entity.getKorisnik().getEmail() : null,
                entity.getGostEmail(),
                entity.getGostPristupniToken(),
                stavkeDto,
                entity.getStatus() != null ? entity.getStatus().name() : null,
                entity.getUkupanIznos(),
                entity.getDatumKreiranja(),
                entity.getPaymentProvider() != null ? entity.getPaymentProvider().name() : null,
                entity.getProviderOrderId(),
                entity.getPaymentCapturedAt(),
                entity.getNacinPlacanja() != null ? entity.getNacinPlacanja().name() : null,
                entity.getAdresaIme(),
                entity.getAdresaUlica(),
                entity.getAdresaGrad(),
                entity.getAdresaPostanskiBroj(),
                entity.getAdresaDrzava(),
                entity.getAdresaTelefon(),
                entity.getVidljiv());
    }

    @Override
    protected Narudzbina convertToEntity(NarudzbinaDTO dto) {
        Narudzbina narudzbina = new Narudzbina();
        narudzbina.setId(dto.getId());
        if (dto.getKorisnikId() != null) {
            narudzbina.setKorisnik(resolveKorisnik(dto.getKorisnikId()));
        } else {
            narudzbina.setKorisnik(null);
        }
        narudzbina.setGostEmail(dto.getGostEmail());
        narudzbina.setGostPristupniToken(dto.getGostPristupniToken());
        narudzbina.setStatus(resolveStatus(dto.getStatus()));
        narudzbina.setDatumKreiranja(dto.getDatumKreiranja() != null ? dto.getDatumKreiranja() : narudzbina.getDatumKreiranja());
        narudzbina.setPaymentProvider(resolvePaymentProvider(dto.getPaymentProvider()));
        narudzbina.setProviderOrderId(dto.getProviderOrderId());
        narudzbina.setPaymentCapturedAt(dto.getPaymentCapturedAt());
        mapAdresaINacin(narudzbina, dto);
        narudzbina.setVidljiv(dto.getVidljiv() == null ? true : dto.getVidljiv());

        List<StavkaNarudzbine> stavke = mapStavke(dto.getStavke(), narudzbina);
        narudzbina.setStavke(stavke);
        narudzbina.setUkupanIznos(izracunajUkupanIznos(stavke));
        return narudzbina;
    }

    @Override
    protected void updateEntityFromDto(NarudzbinaDTO dto, Narudzbina entity) {
        if (dto.getKorisnikId() != null) {
            entity.setKorisnik(resolveKorisnik(dto.getKorisnikId()));
        } else {
            entity.setKorisnik(null);
        }
        if (dto.getGostEmail() != null) {
            entity.setGostEmail(dto.getGostEmail());
        }
        if (dto.getGostPristupniToken() != null) {
            entity.setGostPristupniToken(dto.getGostPristupniToken());
        }
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            entity.setStatus(resolveStatus(dto.getStatus()));
        }
        if (dto.getPaymentProvider() != null && !dto.getPaymentProvider().isBlank()) {
            entity.setPaymentProvider(resolvePaymentProvider(dto.getPaymentProvider()));
        }
        if (dto.getProviderOrderId() != null) {
            entity.setProviderOrderId(dto.getProviderOrderId());
        }
        if (dto.getPaymentCapturedAt() != null) {
            entity.setPaymentCapturedAt(dto.getPaymentCapturedAt());
        }
        if (dto.getVidljiv() != null) {
            entity.setVidljiv(dto.getVidljiv());
        }
        mapAdresaINacin(entity, dto);

        List<StavkaNarudzbine> noveStavke = mapStavke(dto.getStavke(), entity);
        entity.getStavke().clear();
        entity.getStavke().addAll(noveStavke);
        entity.setUkupanIznos(izracunajUkupanIznos(entity.getStavke()));
    }

    public List<NarudzbinaDTO> findMojeNarudzbineByEmail(String email) {
        Korisnik korisnik = korisnikRepository.findByEmail(email).orElse(null);
        if (korisnik == null) {
            return List.of();
        }

        return narudzbinaRepository.findByKorisnikIdAndVidljivTrueOrderByDatumKreiranjaDesc(korisnik.getId())
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

   
    public NarudzbinaDTO saveGuestOrder(NarudzbinaDTO dto) {
        validirajKasuZaCheckout(dto);
        validirajGostEmail(dto.getGostEmail());
        dto.setKorisnikId(null);
        dto.setGostPristupniToken(UUID.randomUUID().toString());

        podesiPocetniStatusPoNacinu(dto);
        NarudzbinaDTO sacuvana = save(dto);
        oznaciPloceKaoNevidljive(sacuvana.getId());
        posaljiRacunAkoTreba(sacuvana.getId());
        return sacuvana;
    }

    public NarudzbinaDTO saveAdminNarudzbinu(NarudzbinaDTO dto) {
        if (dto.getKorisnikId() == null) {
            throw new BadRequestException("Admin kreirana narudzbina mora imati korisnikId");
        }
        if (dto.getGostEmail() != null && !dto.getGostEmail().isBlank()) {
            throw new BadRequestException("Koristite gostEmail samo na /guest endpointu");
        }
        return save(dto);
    }

    public NarudzbinaDTO getGuestNarudzbinaPregled(Long id, String gostToken) {
        Narudzbina narudzbina = findAuthorizedNarudzbina(id, null, false, gostToken);
        return convertToDTO(narudzbina);
    }

    public NarudzbinaDTO payMock(Long narudzbinaId, String email, boolean admin, String gostToken) {
        Narudzbina narudzbina = findAuthorizedNarudzbina(narudzbinaId, email, admin, gostToken);
        if (narudzbina.getStatus() == StatusNarudzbine.PAID) {
            return convertToDTO(narudzbina);
        }
        if (narudzbina.getStatus() == StatusNarudzbine.CANCELLED) {
            throw new BadRequestException("Nije moguce platiti otkazanu narudzbinu");
        }

        narudzbina.setStatus(StatusNarudzbine.PAID);
        narudzbina.setPaymentCapturedAt(LocalDateTime.now());
        narudzbinaRepository.save(narudzbina);
        racunEmailService.posaljiDigitalniRacunAkoMoguce(narudzbina);
        return convertToDTO(narudzbina);
    }

    public PayPalCreateOrderResponseDTO createPayPalOrder(Long narudzbinaId, String email, boolean admin, String gostToken) {
        Narudzbina narudzbina = findAuthorizedNarudzbina(narudzbinaId, email, admin, gostToken);
        if (narudzbina.getNacinPlacanja() != NacinPlacanja.PAYPAL) {
            throw new BadRequestException("PayPal je dostupan samo za narudzbinu sa nacinom placanja PAYPAL");
        }
        if (narudzbina.getStatus() == StatusNarudzbine.PAID) {
            throw new BadRequestException("Narudzbina je vec placena");
        }
        if (narudzbina.getUkupanIznos() == null || narudzbina.getUkupanIznos().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Narudzbina nema validan ukupan iznos za placanje");
        }

        long oid = narudzbina.getId();
        String returnWithOrder = paypalUrlWithQueryParam(paypalReturnUrl, "orderId", String.valueOf(oid));
        String cancelWithOrder = paypalUrlWithQueryParam(paypalCancelUrl, "orderId", String.valueOf(oid));
        PayPalCreateOrderResponseDTO response = payPalService.createOrder(
                iznosZaPayPal(narudzbina.getUkupanIznos()), paypalCurrencyCode(), returnWithOrder, cancelWithOrder);
        narudzbina.setPaymentProvider(PaymentProvider.PAYPAL);
        narudzbina.setProviderOrderId(response.getPaypalOrderId());
        narudzbina.setStatus(StatusNarudzbine.PENDING_PAYMENT);
        narudzbinaRepository.save(narudzbina);
        return response;
    }

    public PayPalCaptureResponseDTO capturePayPalOrder(Long narudzbinaId, String email, boolean admin, String gostToken) {
        Narudzbina narudzbina = findAuthorizedNarudzbina(narudzbinaId, email, admin, gostToken);
        if (narudzbina.getNacinPlacanja() != NacinPlacanja.PAYPAL) {
            throw new BadRequestException("Ova narudzbina nije tipa PAYPAL");
        }
        if (narudzbina.getProviderOrderId() == null || narudzbina.getProviderOrderId().isBlank()) {
            throw new BadRequestException("PayPal order nije prethodno kreiran");
        }

        String captureStatus = payPalService.captureOrder(narudzbina.getProviderOrderId());
        if (!"COMPLETED".equalsIgnoreCase(captureStatus)) {
            narudzbina.setStatus(StatusNarudzbine.FAILED);
            narudzbinaRepository.save(narudzbina);
            throw new BadRequestException("PayPal capture nije uspesan. Status: " + captureStatus);
        }

        narudzbina.setStatus(StatusNarudzbine.PAID);
        narudzbina.setPaymentCapturedAt(LocalDateTime.now());
        narudzbinaRepository.save(narudzbina);
        racunEmailService.posaljiDigitalniRacunAkoMoguce(narudzbina);
        return new PayPalCaptureResponseDTO(narudzbina.getId(), narudzbina.getStatus().name(), narudzbina.getProviderOrderId());
    }

    private void posaljiRacunAkoTreba(Long narudzbinaId) {
        if (narudzbinaId == null) {
            return;
        }
        narudzbinaRepository.findById(narudzbinaId).ifPresent(n -> {
            if (n.getNacinPlacanja() == NacinPlacanja.POUZEC && n.getStatus() == StatusNarudzbine.POTVRDJENA) {
                racunEmailService.posaljiDigitalniRacunAkoMoguce(n);
            }
        });
    }

    private void oznaciPloceKaoNevidljive(Long narudzbinaId) {
        if (narudzbinaId == null) {
            return;
        }
        narudzbinaRepository.findById(narudzbinaId).ifPresent(n -> {
            for (StavkaNarudzbine stavka : n.getStavke()) {
                Long plocaId = stavka.getPloca() != null ? stavka.getPloca().getId() : null;
                if (plocaId == null) {
                    continue;
                }
                plocaRepository.findById(plocaId).ifPresent(ploca -> {
                    if (!Boolean.FALSE.equals(ploca.getVidljiv())) {
                        ploca.setVidljiv(false);
                        plocaRepository.save(ploca);
                    }
                });
            }
        });
    }

    private Korisnik resolveKorisnik(Long korisnikId) {
        if (korisnikId == null) {
            throw new BadRequestException("korisnikId je obavezan");
        }
        return korisnikRepository.findById(korisnikId)
                .orElseThrow(() -> new BadRequestException("Korisnik sa ID " + korisnikId + " ne postoji"));
    }

    private StatusNarudzbine resolveStatus(String status) {
        if (status == null || status.isBlank()) {
            return StatusNarudzbine.CREATED;
        }
        try {
            return StatusNarudzbine.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Nepodrzan status narudzbine: " + status);
        }
    }

    private PaymentProvider resolvePaymentProvider(String provider) {
        if (provider == null || provider.isBlank()) {
            return null;
        }
        try {
            return PaymentProvider.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Nepodrzan payment provider: " + provider);
        }
    }

    private void mapAdresaINacin(Narudzbina entity, NarudzbinaDTO dto) {
        if (dto.getNacinPlacanja() != null && !dto.getNacinPlacanja().isBlank()) {
            try {
                entity.setNacinPlacanja(NacinPlacanja.valueOf(dto.getNacinPlacanja().trim().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                throw new BadRequestException("Nepodrzan nacin placanja: " + dto.getNacinPlacanja());
            }
        } else {
            entity.setNacinPlacanja(null);
        }
        entity.setAdresaIme(dto.getAdresaIme());
        entity.setAdresaUlica(dto.getAdresaUlica());
        entity.setAdresaGrad(dto.getAdresaGrad());
        entity.setAdresaPostanskiBroj(dto.getAdresaPostanskiBroj());
        entity.setAdresaDrzava(dto.getAdresaDrzava());
        entity.setAdresaTelefon(dto.getAdresaTelefon());
    }

    private void validirajKasuZaCheckout(NarudzbinaDTO dto) {
        if (dto.getStavke() == null || dto.getStavke().isEmpty()) {
            throw new BadRequestException("Korpa mora imati bar jednu stavku");
        }
        resolveNacinPlacanjaObavezno(dto.getNacinPlacanja());
        requireText(dto.getAdresaIme(), "Ime i prezime za dostavu je obavezno");
        requireText(dto.getAdresaUlica(), "Ulica i broj su obavezni");
        requireText(dto.getAdresaGrad(), "Grad je obavezan");
        requireText(dto.getAdresaPostanskiBroj(), "Postanski broj je obavezan");
        requireText(dto.getAdresaDrzava(), "Drzava je obavezna");
        requireText(dto.getAdresaTelefon(), "Telefon je obavezan");
    }

    private void validirajGostEmail(String email) {
        requireText(email, "Mejl kupca je obavezan");
        String t = email.trim();
        if (!EMAIL_LABAV.matcher(t).matches()) {
            throw new BadRequestException("Unesite ispravnu e-adresu");
        }
    }

    private void podesiPocetniStatusPoNacinu(NarudzbinaDTO dto) {
        NacinPlacanja nacin = resolveNacinPlacanjaObavezno(dto.getNacinPlacanja());
        if (nacin == NacinPlacanja.POUZEC) {
            dto.setStatus(StatusNarudzbine.POTVRDJENA.name());
            dto.setPaymentProvider(null);
            dto.setProviderOrderId(null);
        } else if (nacin == NacinPlacanja.PAYPAL) {
            dto.setStatus(StatusNarudzbine.PENDING_PAYMENT.name());
        }
    }

    private void requireText(String vrednost, String poruka) {
        if (vrednost == null || vrednost.isBlank()) {
            throw new BadRequestException(poruka);
        }
    }

    private static String paypalUrlWithQueryParam(String base, String name, String value) {
        if (base == null || base.isBlank()) {
            return "?" + name + "=" + value;
        }
        String sep = base.contains("?") ? "&" : "?";
        return base + sep + name + "=" + value;
    }

    private BigDecimal iznosZaPayPal(BigDecimal iznosRsd) {
        if ("RSD".equals(paypalCurrencyCode())) {
            return iznosRsd.setScale(2, RoundingMode.HALF_UP);
        }
        return iznosRsd.multiply(exchangeRateService.rsdTo(paypalCurrencyCode())).setScale(2, RoundingMode.HALF_UP);
    }

    private String paypalCurrencyCode() {
        if (paypalPaymentCurrency == null || paypalPaymentCurrency.isBlank()) {
            return "EUR";
        }
        return paypalPaymentCurrency.trim().toUpperCase();
    }

    private NacinPlacanja resolveNacinPlacanjaObavezno(String s) {
        if (s == null || s.isBlank()) {
            throw new BadRequestException("Nacin placanja je obavezan (PAYPAL ili POUZEC)");
        }
        try {
            return NacinPlacanja.valueOf(s.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Nacin placanja mora biti PAYPAL ili POUZEC");
        }
    }

    private List<StavkaNarudzbine> mapStavke(List<StavkaNarudzbineDTO> stavkeDto, Narudzbina narudzbina) {
        List<StavkaNarudzbine> stavke = new ArrayList<>();
        if (stavkeDto == null) {
            return stavke;
        }
        Set<Long> seenPlocaIds = new HashSet<>();

        for (StavkaNarudzbineDTO dto : stavkeDto) {
            Long plocaId = dto.getPlocaId();
            if (plocaId == null) {
                throw new BadRequestException("plocaId je obavezan za svaku stavku narudzbine");
            }
            if (!seenPlocaIds.add(plocaId)) {
                throw new BadRequestException("Duplikat stavke nije dozvoljen za plocu sa ID " + plocaId);
            }

            Ploca ploca = plocaRepository.findById(plocaId)
                    .orElseThrow(() -> new BadRequestException("Ploca sa ID " + plocaId + " ne postoji"));

            BigDecimal jedinicnaCena = dto.getJedinicnaCena() != null
                    ? dto.getJedinicnaCena()
                    : ploca.getProizvod().getCena();

            StavkaNarudzbine stavka = new StavkaNarudzbine();
            stavka.setId(dto.getId());
            stavka.setNarudzbina(narudzbina);
            stavka.setPloca(ploca);
            stavka.setJedinicnaCena(jedinicnaCena);
            stavka.setUkupno(jedinicnaCena);
            stavka.setVidljiv(dto.getVidljiv() == null ? true : dto.getVidljiv());
            stavke.add(stavka);
        }

        return stavke;
    }

    private BigDecimal izracunajUkupanIznos(List<StavkaNarudzbine> stavke) {
        return stavke.stream()
                .map(StavkaNarudzbine::getUkupno)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Narudzbina findAuthorizedNarudzbina(Long narudzbinaId, String email, boolean admin, String gostToken) {
        if (narudzbinaId == null) {
            throw new BadRequestException("id narudzbine je obavezan");
        }
        Narudzbina narudzbina = narudzbinaRepository.findById(narudzbinaId)
                .orElseThrow(() -> new BadRequestException("Narudzbina sa ID " + narudzbinaId + " ne postoji"));

        if (admin) {
            return narudzbina;
        }

        if (narudzbina.getGostPristupniToken() != null) {
            if (gostToken == null || gostToken.isBlank()
                    || !narudzbina.getGostPristupniToken().equals(gostToken.trim())) {
                throw new BadRequestException("Nedostaje ili je neispravan pristup. Koristite header X-Order-Token.");
            }
            return narudzbina;
        }

        if (email == null) {
            throw new BadRequestException("Morate biti ulogovani");
        }
        String ownerEmail = narudzbina.getKorisnik() != null ? narudzbina.getKorisnik().getEmail() : null;
        if (ownerEmail == null || !ownerEmail.equalsIgnoreCase(email)) {
            throw new BadRequestException("Nemate pravo pristupa ovoj narudzbini");
        }
        return narudzbina;
    }
}

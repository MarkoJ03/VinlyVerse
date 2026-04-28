package server.service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import server.model.Narudzbina;
import server.model.StavkaNarudzbine;

@Service
public class RacunEmailService {

    private static final Logger log = LoggerFactory.getLogger(RacunEmailService.class);
    private static final DateTimeFormatter DATUM_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final JavaMailSender mailSender;
    private final boolean mailEnabled;
    private final String fromEmail;

    public RacunEmailService(
            JavaMailSender mailSender,
            @Value("${app.mail.enabled:false}") boolean mailEnabled,
            @Value("${app.mail.from:no-reply@vinylverse.local}") String fromEmail) {
        this.mailSender = mailSender;
        this.mailEnabled = mailEnabled;
        this.fromEmail = fromEmail;
    }

    public void posaljiDigitalniRacunAkoMoguce(Narudzbina narudzbina) {
        String recipient = resolveRecipientEmail(narudzbina);
        if (recipient == null) {
            log.warn("Preskacem slanje racuna: narudzbina {} nema email primaoca.", narudzbina != null ? narudzbina.getId() : null);
            return;
        }
        if (!mailEnabled) {
            log.info("Mail je iskljucen (app.mail.enabled=false). Racun za narudzbinu {} nije poslat.", narudzbina.getId());
            return;
        }

        try {
            SimpleMailMessage poruka = new SimpleMailMessage();
            poruka.setFrom(fromEmail);
            poruka.setTo(recipient);
            poruka.setSubject("VinylVerse digitalni racun #" + narudzbina.getId());
            poruka.setText(buildRacunBody(narudzbina));
            mailSender.send(poruka);
        } catch (Exception ex) {
            log.error("Neuspesno slanje digitalnog racuna za narudzbinu {} na {}.", narudzbina.getId(), recipient, ex);
        }
    }

    private String resolveRecipientEmail(Narudzbina narudzbina) {
        if (narudzbina == null) {
            return null;
        }
        if (narudzbina.getGostEmail() != null && !narudzbina.getGostEmail().isBlank()) {
            return narudzbina.getGostEmail().trim();
        }
        if (narudzbina.getKorisnik() != null && narudzbina.getKorisnik().getEmail() != null
                && !narudzbina.getKorisnik().getEmail().isBlank()) {
            return narudzbina.getKorisnik().getEmail().trim();
        }
        return null;
    }

    private String buildRacunBody(Narudzbina narudzbina) {
        StringBuilder sb = new StringBuilder();
        sb.append("Hvala na kupovini u VinylVerse.\n\n");
        sb.append("Digitalni racun\n");
        sb.append("Broj narudzbine: #").append(narudzbina.getId()).append('\n');
        if (narudzbina.getDatumKreiranja() != null) {
            sb.append("Datum: ").append(DATUM_FORMAT.format(narudzbina.getDatumKreiranja())).append('\n');
        }
        sb.append("Nacin placanja: ").append(narudzbina.getNacinPlacanja()).append('\n');
        sb.append("Status: ").append(narudzbina.getStatus()).append("\n\n");
        sb.append("Stavke:\n");
        for (StavkaNarudzbine stavka : narudzbina.getStavke()) {
            String naziv = (stavka.getPloca() != null && stavka.getPloca().getProizvod() != null
                    && stavka.getPloca().getProizvod().getNaziv() != null)
                            ? stavka.getPloca().getProizvod().getNaziv()
                            : "Nepoznat proizvod";
            BigDecimal cena = Objects.requireNonNullElse(stavka.getUkupno(), BigDecimal.ZERO);
            sb.append("- ").append(naziv).append(" | ").append(cena).append(" RSD\n");
        }
        sb.append('\n');
        sb.append("Ukupno: ").append(Objects.requireNonNullElse(narudzbina.getUkupanIznos(), BigDecimal.ZERO)).append(" RSD\n\n");
        sb.append("Adresa dostave:\n");
        sb.append(Objects.toString(narudzbina.getAdresaIme(), "")).append('\n');
        sb.append(Objects.toString(narudzbina.getAdresaUlica(), "")).append('\n');
        sb.append(Objects.toString(narudzbina.getAdresaPostanskiBroj(), "")).append(' ')
                .append(Objects.toString(narudzbina.getAdresaGrad(), "")).append('\n');
        sb.append(Objects.toString(narudzbina.getAdresaDrzava(), "")).append('\n');
        sb.append("Telefon: ").append(Objects.toString(narudzbina.getAdresaTelefon(), "")).append("\n\n");
        sb.append("Ovo je automatski generisana poruka.");
        return sb.toString();
    }
}

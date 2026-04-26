package server.DTOs;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NarudzbinaDTO {

    private Long id;
    private Long korisnikId;
    private String korisnikEmail;
    private String gostEmail;
    private String gostPristupniToken;
    private List<StavkaNarudzbineDTO> stavke = new ArrayList<>();
    private String status;
    private BigDecimal ukupanIznos;
    private LocalDateTime datumKreiranja;
    private String paymentProvider;
    private String providerOrderId;
    private LocalDateTime paymentCapturedAt;
    private String nacinPlacanja;
    private String adresaIme;
    private String adresaUlica;
    private String adresaGrad;
    private String adresaPostanskiBroj;
    private String adresaDrzava;
    private String adresaTelefon;
    private Boolean vidljiv = true;
}

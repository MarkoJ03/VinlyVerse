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
    private List<StavkaNarudzbineDTO> stavke = new ArrayList<>();
    private String status;
    private BigDecimal ukupanIznos;
    private LocalDateTime datumKreiranja;
    private Boolean vidljiv = true;
}

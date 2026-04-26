package server.DTOs;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class StavkaNarudzbineDTO {

    private Long id;
    private Long narudzbinaId;
    private Long plocaId;
    private String nazivPloce;
    private BigDecimal jedinicnaCena;
    private BigDecimal ukupno;
    private Boolean vidljiv = true;
}

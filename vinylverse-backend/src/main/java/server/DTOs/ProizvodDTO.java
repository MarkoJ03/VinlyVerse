package server.DTOs;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProizvodDTO {

private Long id;
	

	private String naziv;
	

    private BigDecimal cena;
	

 
    private Boolean vidljiv;
}

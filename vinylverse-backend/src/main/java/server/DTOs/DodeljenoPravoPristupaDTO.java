package server.DTOs;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import server.model.Korisnik;
import server.model.PravoPristupa;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DodeljenoPravoPristupaDTO {
	 private Long id;


	    private Korisnik korisnik;


	    private PravoPristupaDTO pravoPristupa;
	    

	    private Boolean vidljiv = true;
}

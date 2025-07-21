package server.DTOs;

import java.math.BigDecimal;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import server.model.DodeljenoPravoPristupa;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PravoPristupaDTO {
	private Long id;

	private String naziv;
	
	private Set<DodeljenoPravoPristupa> dodeljenaPravaPristupa;
	

    private Boolean vidljiv = true;
}

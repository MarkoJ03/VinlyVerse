package server.DTOs;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import server.model.Proizvod;
import server.model.Zanr;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PlocaDTO {
	private Long id;


    private ProizvodDTO proizvod;


    private String listaPesama;


    private String brend;


    private String izdavackaKuca;


    private Zanr zanr;


    private Boolean vidljiv;
}

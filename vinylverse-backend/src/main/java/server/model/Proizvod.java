package server.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class Proizvod {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	
	@Column(nullable = false)
	private String naziv;
	
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal cena;
	

    @Column(nullable = false)
    private Boolean vidljiv;
}

package server.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proizvod {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	
	@Column(nullable = false)
	private String naziv;
	
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal cena;
	
	@Column(nullable = false, length = 10000)
    private String opis;
	
	@Column
	private String slikaPutanja;

    @Column(nullable = false)
    private Boolean vidljiv;
}

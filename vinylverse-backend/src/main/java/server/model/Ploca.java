package server.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ploca {

	@Id
    private Long id;

	@OneToOne(cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "id")
    private Proizvod proizvod;
	@Column(nullable = false, length = 10000)
    private String listaPesama;
	@Column(nullable = false)

    private String brend;
	@Column(nullable = false)

    private String izdavackaKuca;
	
	@Column(nullable = false)
	private String godinaIzdanja;

	@ManyToOne(optional = false) 
    private Zanr zanr;
	@Column(nullable = false)

    private Boolean vidljiv;
    
     
    
    
    
    
}

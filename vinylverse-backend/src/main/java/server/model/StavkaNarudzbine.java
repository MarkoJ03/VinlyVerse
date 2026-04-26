package server.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StavkaNarudzbine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "narudzbina_id", nullable = false)
    private Narudzbina narudzbina;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ploca_id", nullable = false)
    private Ploca ploca;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal jedinicnaCena;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal ukupno;

    @Column(nullable = false)
    private Boolean vidljiv = true;
}

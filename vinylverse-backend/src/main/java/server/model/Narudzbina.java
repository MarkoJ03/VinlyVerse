package server.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Narudzbina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "korisnik_id", nullable = false)
    private Korisnik korisnik;

    @OneToMany(mappedBy = "narudzbina", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StavkaNarudzbine> stavke = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusNarudzbine status = StatusNarudzbine.CREATED;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal ukupanIznos = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime datumKreiranja = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean vidljiv = true;
}

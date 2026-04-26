package server.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @ManyToOne(optional = true)
    @JoinColumn(name = "korisnik_id", nullable = true)
    private Korisnik korisnik;


    @Column(length = 200)
    private String gostEmail;


    @Column(length = 64, unique = true)
    private String gostPristupniToken;

    @OneToMany(mappedBy = "narudzbina", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StavkaNarudzbine> stavke = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 50, columnDefinition = "varchar(50)")
    private StatusNarudzbine status = StatusNarudzbine.CREATED;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal ukupanIznos = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime datumKreiranja = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 50, columnDefinition = "varchar(50)")
    private PaymentProvider paymentProvider;

    @Column
    private String providerOrderId;

    @Column
    private LocalDateTime paymentCapturedAt;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 50, columnDefinition = "varchar(50)")
    private NacinPlacanja nacinPlacanja;

    @Column(length = 200)
    private String adresaIme;

    @Column(length = 300)
    private String adresaUlica;

    @Column(length = 100)
    private String adresaGrad;

    @Column(length = 20)
    private String adresaPostanskiBroj;

    @Column(length = 100)
    private String adresaDrzava;

    @Column(length = 50)
    private String adresaTelefon;

    @Column(nullable = false)
    private Boolean vidljiv = true;
}

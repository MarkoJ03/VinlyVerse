package server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import server.model.Narudzbina;

public interface NarudzbinaRepository extends JpaRepository<Narudzbina, Long> {
    List<Narudzbina> findByKorisnikIdAndVidljivTrueOrderByDatumKreiranjaDesc(Long korisnikId);
}

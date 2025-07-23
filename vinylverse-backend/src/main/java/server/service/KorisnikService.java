package server.service;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import server.DTOs.DodeljenoPravoPristupaDTO;
import server.DTOs.KorisnikDTO;
import server.model.DodeljenoPravoPristupa;
import server.model.Korisnik;
import server.model.PravoPristupa;
import server.repository.KorisnikRepository;
import server.repository.PravoPristupaRepository;

@Service
public class KorisnikService extends BaseService<Korisnik, KorisnikDTO, Long> {

    @Autowired
    private KorisnikRepository korisnikRepository;

    @Autowired
    @Lazy
    private DodeljenoPravoPristupaService dodeljenoPravoPristupaService;

    @Autowired
    private PravoPristupaRepository pravoPristupaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected CrudRepository<Korisnik, Long> getRepository() {
        return korisnikRepository;
    }

    @Override
    protected KorisnikDTO convertToDTO(Korisnik entity) {
        Set<DodeljenoPravoPristupaDTO> pravaDTO = new HashSet<>();
        if (entity.getDodeljenaPravaPristupa() != null) {
            for (DodeljenoPravoPristupa pravo : entity.getDodeljenaPravaPristupa()) {
                pravaDTO.add(dodeljenoPravoPristupaService.convertToDTO(pravo));
            }
        }
        return new KorisnikDTO(
                entity.getId(),
                entity.getEmail(),
                entity.getKorisnickoIme(),
                null,
                pravaDTO,
                entity.getVidljiv()
        );
    }

    @Override
    protected Korisnik convertToEntity(KorisnikDTO dto) {
        Korisnik korisnik = new Korisnik();
        korisnik.setId(dto.getId());
        korisnik.setEmail(dto.getEmail());
        korisnik.setKorisnickoIme(dto.getKorisnickoIme());
        korisnik.setLozinka(passwordEncoder.encode(dto.getLozinka()));
        Set<DodeljenoPravoPristupa> pravaEntiteti = new HashSet<>();
        if (dto.getDodeljenaPravaPristupa() != null) {
            for (DodeljenoPravoPristupaDTO pravoDTO : dto.getDodeljenaPravaPristupa()) {
                DodeljenoPravoPristupa dp = dodeljenoPravoPristupaService.convertToEntity(pravoDTO);
                dp.setKorisnik(korisnik);
                pravaEntiteti.add(dp);
            }
        }
        korisnik.setDodeljenaPravaPristupa(pravaEntiteti);
        korisnik.setVidljiv(dto.getVidljiv());
        return korisnik;
    }

    @Override
    protected void updateEntityFromDto(KorisnikDTO dto, Korisnik entity) {
        entity.setEmail(dto.getEmail());
        entity.setKorisnickoIme(dto.getKorisnickoIme());
        if (dto.getLozinka() != null && !dto.getLozinka().isBlank()) {
            entity.setLozinka(passwordEncoder.encode(dto.getLozinka()));
        }
        entity.setVidljiv(dto.getVidljiv());

        var existingPravaMap = entity.getDodeljenaPravaPristupa().stream()
                .filter(dp -> dp.getId() != null)
                .collect(Collectors.toMap(DodeljenoPravoPristupa::getId, Function.identity()));

        var dtoPravaIds = dto.getDodeljenaPravaPristupa() != null
                ? dto.getDodeljenaPravaPristupa().stream()
                    .filter(dtoPravo -> dtoPravo.getId() != null)
                    .map(DodeljenoPravoPristupaDTO::getId)
                    .collect(Collectors.toSet())
                : Set.of();

        entity.getDodeljenaPravaPristupa().removeIf(dp -> dp.getId() != null && !dtoPravaIds.contains(dp.getId()));

        if (dto.getDodeljenaPravaPristupa() != null) {
            for (DodeljenoPravoPristupaDTO dtoPravo : dto.getDodeljenaPravaPristupa()) {
                DodeljenoPravoPristupa dp = dtoPravo.getId() != null && existingPravaMap.containsKey(dtoPravo.getId())
                        ? existingPravaMap.get(dtoPravo.getId())
                        : new DodeljenoPravoPristupa();
                dp.setVidljiv(dtoPravo.getVidljiv());
                dp.setKorisnik(entity);
                if (dtoPravo.getPravoPristupa() != null && dtoPravo.getPravoPristupa().getId() != null) {
                    var pravo = pravoPristupaRepository.findById(dtoPravo.getPravoPristupa().getId()).orElse(null);
                    dp.setPravoPristupa(pravo);
                }
                entity.getDodeljenaPravaPristupa().add(dp);
            }
        }
    }

//    public Korisnik findByEmailAndLozinka(String email, String lozinka) {
//        return korisnikRepository.findByEmailAndLozinka(email, lozinka).orElse(null);
//    }
//
//    @Transactional
//    public Korisnik findByEmail(String email) {
//        return korisnikRepository.findByEmail(email).orElse(null);
//    }
//
//    @Transactional
//    public Korisnik findByEmailWithPrivileges(String email) {
//        return korisnikRepository.findByEmailWithDodeljenaPravaPristupa(email);
//    }
}

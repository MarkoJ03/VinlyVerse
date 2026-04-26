package server.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import server.DTOs.NarudzbinaDTO;
import server.DTOs.StavkaNarudzbineDTO;
import server.exception.BadRequestException;
import server.model.Korisnik;
import server.model.Narudzbina;
import server.model.Ploca;
import server.model.StatusNarudzbine;
import server.model.StavkaNarudzbine;
import server.repository.KorisnikRepository;
import server.repository.NarudzbinaRepository;
import server.repository.PlocaRepository;

@Service
public class NarudzbinaService extends BaseService<Narudzbina, NarudzbinaDTO, Long> {

    @Autowired
    private NarudzbinaRepository narudzbinaRepository;

    @Autowired
    private KorisnikRepository korisnikRepository;

    @Autowired
    private PlocaRepository plocaRepository;

    @Override
    protected CrudRepository<Narudzbina, Long> getRepository() {
        return narudzbinaRepository;
    }

    @Override
    protected NarudzbinaDTO convertToDTO(Narudzbina entity) {
        List<StavkaNarudzbineDTO> stavkeDto = new ArrayList<>();
        if (entity.getStavke() != null) {
            for (StavkaNarudzbine stavka : entity.getStavke()) {
                String nazivPloce = stavka.getPloca() != null && stavka.getPloca().getProizvod() != null
                        ? stavka.getPloca().getProizvod().getNaziv()
                        : null;
                stavkeDto.add(new StavkaNarudzbineDTO(
                        stavka.getId(),
                        entity.getId(),
                        stavka.getPloca() != null ? stavka.getPloca().getId() : null,
                        nazivPloce,
                        stavka.getJedinicnaCena(),
                        stavka.getUkupno(),
                        stavka.getVidljiv()));
            }
        }

        return new NarudzbinaDTO(
                entity.getId(),
                entity.getKorisnik() != null ? entity.getKorisnik().getId() : null,
                entity.getKorisnik() != null ? entity.getKorisnik().getEmail() : null,
                stavkeDto,
                entity.getStatus() != null ? entity.getStatus().name() : null,
                entity.getUkupanIznos(),
                entity.getDatumKreiranja(),
                entity.getVidljiv());
    }

    @Override
    protected Narudzbina convertToEntity(NarudzbinaDTO dto) {
        Narudzbina narudzbina = new Narudzbina();
        narudzbina.setId(dto.getId());
        narudzbina.setKorisnik(resolveKorisnik(dto.getKorisnikId()));
        narudzbina.setStatus(resolveStatus(dto.getStatus()));
        narudzbina.setDatumKreiranja(dto.getDatumKreiranja() != null ? dto.getDatumKreiranja() : narudzbina.getDatumKreiranja());
        narudzbina.setVidljiv(dto.getVidljiv() == null ? true : dto.getVidljiv());

        List<StavkaNarudzbine> stavke = mapStavke(dto.getStavke(), narudzbina);
        narudzbina.setStavke(stavke);
        narudzbina.setUkupanIznos(izracunajUkupanIznos(stavke));
        return narudzbina;
    }

    @Override
    protected void updateEntityFromDto(NarudzbinaDTO dto, Narudzbina entity) {
        if (dto.getKorisnikId() != null) {
            entity.setKorisnik(resolveKorisnik(dto.getKorisnikId()));
        }
        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            entity.setStatus(resolveStatus(dto.getStatus()));
        }
        if (dto.getVidljiv() != null) {
            entity.setVidljiv(dto.getVidljiv());
        }

        List<StavkaNarudzbine> noveStavke = mapStavke(dto.getStavke(), entity);
        entity.getStavke().clear();
        entity.getStavke().addAll(noveStavke);
        entity.setUkupanIznos(izracunajUkupanIznos(entity.getStavke()));
    }

    public List<NarudzbinaDTO> findMojeNarudzbineByEmail(String email) {
        Korisnik korisnik = korisnikRepository.findByEmail(email).orElse(null);
        if (korisnik == null) {
            return List.of();
        }

        return narudzbinaRepository.findByKorisnikIdAndVidljivTrueOrderByDatumKreiranjaDesc(korisnik.getId())
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public NarudzbinaDTO saveForAuthenticatedUser(String email, NarudzbinaDTO dto) {
        Korisnik korisnik = korisnikRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("Korisnik sa email-om " + email + " ne postoji"));
        dto.setKorisnikId(korisnik.getId());
        return save(dto);
    }

    private Korisnik resolveKorisnik(Long korisnikId) {
        if (korisnikId == null) {
            throw new BadRequestException("korisnikId je obavezan");
        }
        return korisnikRepository.findById(korisnikId)
                .orElseThrow(() -> new BadRequestException("Korisnik sa ID " + korisnikId + " ne postoji"));
    }

    private StatusNarudzbine resolveStatus(String status) {
        if (status == null || status.isBlank()) {
            return StatusNarudzbine.CREATED;
        }
        try {
            return StatusNarudzbine.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Nepodrzan status narudzbine: " + status);
        }
    }

    private List<StavkaNarudzbine> mapStavke(List<StavkaNarudzbineDTO> stavkeDto, Narudzbina narudzbina) {
        List<StavkaNarudzbine> stavke = new ArrayList<>();
        if (stavkeDto == null) {
            return stavke;
        }
        Set<Long> seenPlocaIds = new HashSet<>();

        for (StavkaNarudzbineDTO dto : stavkeDto) {
            Long plocaId = dto.getPlocaId();
            if (plocaId == null) {
                throw new BadRequestException("plocaId je obavezan za svaku stavku narudzbine");
            }
            if (!seenPlocaIds.add(plocaId)) {
                throw new BadRequestException("Duplikat stavke nije dozvoljen za plocu sa ID " + plocaId);
            }

            Ploca ploca = plocaRepository.findById(plocaId)
                    .orElseThrow(() -> new BadRequestException("Ploca sa ID " + plocaId + " ne postoji"));

            BigDecimal jedinicnaCena = dto.getJedinicnaCena() != null
                    ? dto.getJedinicnaCena()
                    : ploca.getProizvod().getCena();

            StavkaNarudzbine stavka = new StavkaNarudzbine();
            stavka.setId(dto.getId());
            stavka.setNarudzbina(narudzbina);
            stavka.setPloca(ploca);
            stavka.setJedinicnaCena(jedinicnaCena);
            stavka.setUkupno(jedinicnaCena);
            stavka.setVidljiv(dto.getVidljiv() == null ? true : dto.getVidljiv());
            stavke.add(stavka);
        }

        return stavke;
    }

    private BigDecimal izracunajUkupanIznos(List<StavkaNarudzbine> stavke) {
        return stavke.stream()
                .map(StavkaNarudzbine::getUkupno)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

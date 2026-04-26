package server.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import server.DTOs.StavkaNarudzbineDTO;
import server.model.Narudzbina;
import server.model.Ploca;
import server.model.StavkaNarudzbine;
import server.repository.NarudzbinaRepository;
import server.repository.PlocaRepository;
import server.repository.StavkaNarudzbineRepository;

@Service
public class StavkaNarudzbineService extends BaseService<StavkaNarudzbine, StavkaNarudzbineDTO, Long> {

    @Autowired
    private StavkaNarudzbineRepository stavkaNarudzbineRepository;

    @Autowired
    private PlocaRepository plocaRepository;

    @Autowired
    private NarudzbinaRepository narudzbinaRepository;

    @Override
    protected CrudRepository<StavkaNarudzbine, Long> getRepository() {
        return stavkaNarudzbineRepository;
    }

    @Override
    protected StavkaNarudzbineDTO convertToDTO(StavkaNarudzbine entity) {
        String nazivPloce = entity.getPloca() != null && entity.getPloca().getProizvod() != null
                ? entity.getPloca().getProizvod().getNaziv()
                : null;
        return new StavkaNarudzbineDTO(
                entity.getId(),
                entity.getNarudzbina() != null ? entity.getNarudzbina().getId() : null,
                entity.getPloca() != null ? entity.getPloca().getId() : null,
                nazivPloce,
                entity.getJedinicnaCena(),
                entity.getUkupno(),
                entity.getVidljiv());
    }

    @Override
    protected StavkaNarudzbine convertToEntity(StavkaNarudzbineDTO dto) {
        StavkaNarudzbine entity = new StavkaNarudzbine();
        entity.setId(dto.getId());
        entity.setPloca(resolvePloca(dto.getPlocaId()));
        entity.setNarudzbina(resolveNarudzbina(dto.getNarudzbinaId()));

        BigDecimal jedinicnaCena = dto.getJedinicnaCena() != null
                ? dto.getJedinicnaCena()
                : entity.getPloca().getProizvod().getCena();
        entity.setJedinicnaCena(jedinicnaCena);
        entity.setUkupno(jedinicnaCena);
        entity.setVidljiv(dto.getVidljiv() == null ? true : dto.getVidljiv());
        return entity;
    }

    @Override
    protected void updateEntityFromDto(StavkaNarudzbineDTO dto, StavkaNarudzbine entity) {
        if (dto.getPlocaId() != null) {
            entity.setPloca(resolvePloca(dto.getPlocaId()));
        }

        BigDecimal jedinicnaCena = dto.getJedinicnaCena() != null
                ? dto.getJedinicnaCena()
                : entity.getPloca().getProizvod().getCena();
        entity.setJedinicnaCena(jedinicnaCena);
        entity.setUkupno(jedinicnaCena);
        if (dto.getVidljiv() != null) {
            entity.setVidljiv(dto.getVidljiv());
        }
    }

    private Ploca resolvePloca(Long plocaId) {
        if (plocaId == null) {
            throw new RuntimeException("plocaId je obavezan");
        }
        return plocaRepository.findById(plocaId)
                .orElseThrow(() -> new RuntimeException("Ploca sa ID " + plocaId + " ne postoji"));
    }

    private Narudzbina resolveNarudzbina(Long narudzbinaId) {
        if (narudzbinaId == null) {
            throw new RuntimeException("narudzbinaId je obavezan");
        }
        return narudzbinaRepository.findById(narudzbinaId)
                .orElseThrow(() -> new RuntimeException("Narudzbina sa ID " + narudzbinaId + " ne postoji"));
    }
}

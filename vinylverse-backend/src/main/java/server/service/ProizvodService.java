package server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import server.DTOs.ProizvodDTO;
import server.model.Proizvod;
import server.repository.ProizvodRepository;

@Service
public class ProizvodService extends BaseService<Proizvod, ProizvodDTO, Long> {

    @Autowired
    private ProizvodRepository proizvodRepository;

    @Override
    protected CrudRepository<Proizvod, Long> getRepository() {
        return proizvodRepository;
    }

    @Override
    protected ProizvodDTO convertToDTO(Proizvod entity) {
        return new ProizvodDTO(entity.getId(), entity.getNaziv(), entity.getCena(), entity.getVidljiv());
    }

    @Override
    protected Proizvod convertToEntity(ProizvodDTO dto) {
        Proizvod proizvod = new Proizvod();
        proizvod.setId(dto.getId());
        proizvod.setNaziv(dto.getNaziv());
        proizvod.setCena(dto.getCena());
        proizvod.setVidljiv(dto.getVidljiv());
        return proizvod;
    }

    @Override
    protected void updateEntityFromDto(ProizvodDTO dto, Proizvod entity) {
        entity.setNaziv(dto.getNaziv());
        entity.setCena(dto.getCena());
        entity.setVidljiv(dto.getVidljiv());
    }
}

package server.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import server.DTOs.PravoPristupaDTO;
import server.model.PravoPristupa;
import server.repository.PravoPristupaRepository;

@Service
public class PravoPristupaService extends BaseService<PravoPristupa, PravoPristupaDTO, Long> {

    @Autowired
    private PravoPristupaRepository pravoPristupaRepository;

    @Override
    protected CrudRepository<PravoPristupa, Long> getRepository() {
        return pravoPristupaRepository;
    }

    @Override
    protected PravoPristupaDTO convertToDTO(PravoPristupa entity) {
        return new PravoPristupaDTO(entity.getId(), entity.getNaziv(),null, entity.getVidljiv());
    }

    @Override
    protected PravoPristupa convertToEntity(PravoPristupaDTO dto) {
        return new PravoPristupa(dto.getId(), dto.getNaziv(), null, dto.getVidljiv());
    }

    @Override
    protected void updateEntityFromDto(PravoPristupaDTO dto, PravoPristupa entity) {
        entity.setNaziv(dto.getNaziv());
        entity.setVidljiv(dto.getVidljiv() != null ? dto.getVidljiv() : true);
    }
}

package server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import server.DTOs.PlocaDTO;
import server.DTOs.ZanrDTO;
import server.model.Ploca;
import server.model.Zanr;
import server.repository.ZanrRepository;

@Service
public class ZanrService extends BaseService<Zanr, ZanrDTO, Long> {

    @Autowired
    private ZanrRepository zanrRepository;
    
    @Autowired
    @Lazy
    private PlocaService plocaService;

    @Override
    protected CrudRepository<Zanr, Long> getRepository() {
        return zanrRepository;
    }

    @Override
    protected ZanrDTO convertToDTO(Zanr entity) {
    	List<PlocaDTO> ploce = new ArrayList<PlocaDTO>();
    	
        for (Ploca p : entity.getPloce()) {
        	ploce.add(plocaService.convertToDTO(p));
        }
    	
        return new ZanrDTO(entity.getId(), entity.getNaziv(),ploce, entity.getVidljiv());
    }

    @Override
    protected Zanr convertToEntity(ZanrDTO dto) {
    	List<Ploca> ploce = new ArrayList<Ploca>();
    	
        for (PlocaDTO p : dto.getPloce()) {
        	ploce.add(plocaService.convertToEntity(p));
        }
    	
        Zanr zanr = new Zanr();
        zanr.setId(dto.getId());
        zanr.setNaziv(dto.getNaziv());
        zanr.setPloce(ploce);
        zanr.setVidljiv(dto.getVidljiv());
        return zanr;
    }

    @Override
    protected void updateEntityFromDto(ZanrDTO dto, Zanr entity) {
        entity.setNaziv(dto.getNaziv());
        entity.setVidljiv(dto.getVidljiv());
    }
}

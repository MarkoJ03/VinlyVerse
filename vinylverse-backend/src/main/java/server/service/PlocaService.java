package server.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import server.DTOs.PlocaDTO;
import server.DTOs.ZanrDTO;
import server.model.Ploca;
import server.model.Proizvod;
import server.model.Zanr;
import server.repository.PlocaRepository;

@Service
public class PlocaService extends BaseService<Ploca, PlocaDTO, Long> {

    @Autowired
    private PlocaRepository plocaRepository;

    @Autowired
    @Lazy
    private ZanrService zanrService;

    @Autowired
    @Lazy
    private ProizvodService proizvodService;

    @Override
    protected CrudRepository<Ploca, Long> getRepository() {
        return plocaRepository;
    }

    @Override
    protected PlocaDTO convertToDTO(Ploca entity) {
        return new PlocaDTO(
            entity.getId(),
            proizvodService.convertToDTO(entity.getProizvod()),
            entity.getListaPesama(),
            entity.getBrend(),
            entity.getIzdavackaKuca(),
            new ZanrDTO(entity.getZanr().getId(), entity.getZanr().getNaziv(),null,entity.getZanr().getVidljiv()),
            entity.getVidljiv()
        );
    }

    @Override
    protected Ploca convertToEntity(PlocaDTO dto) {
        Ploca ploca = new Ploca();
        ploca.setId(dto.getId());
        ploca.setProizvod(proizvodService.convertToEntity(dto.getProizvod()));
        ploca.setListaPesama(dto.getListaPesama());
        ploca.setBrend(dto.getBrend());
        ploca.setIzdavackaKuca(dto.getIzdavackaKuca());
        ploca.setZanr (new Zanr(dto.getZanr().getId(), dto.getZanr().getNaziv(),null,dto.getZanr().getVidljiv())
);
        ploca.setVidljiv(dto.getVidljiv());
        return ploca;
    }

    @Override
    protected void updateEntityFromDto(PlocaDTO dto, Ploca entity) {
        entity.setListaPesama(dto.getListaPesama());
        entity.setBrend(dto.getBrend());
        entity.setIzdavackaKuca(dto.getIzdavackaKuca());
        if (dto.getZanr() != null && dto.getZanr().getId() != null) {
            Zanr zanr = new Zanr();
            zanr.setId(dto.getZanr().getId());
            entity.setZanr(zanr);
        } else {
            entity.setZanr(null);
        }
    }
    
    public List<PlocaDTO> getPaginiranePloce(int page, int size) {
        List<Ploca> svePloce = new ArrayList<>();
        plocaRepository.findAll().forEach(svePloce::add); 

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, svePloce.size());

        if (fromIndex > svePloce.size()) return List.of(); 

        return svePloce.subList(fromIndex, toIndex).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<PlocaDTO> getNasumicnePloce(int broj) {
        List<Ploca> svePloce = new ArrayList<>();
        plocaRepository.findAll().forEach(svePloce::add);

        Collections.shuffle(svePloce);
        return svePloce.stream()
            .limit(broj)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }


}  

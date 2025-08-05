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

import jakarta.transaction.Transactional;
import server.DTOs.PlocaDTO;
import server.DTOs.ZanrDTO;
import server.model.Ploca;
import server.model.Proizvod;
import server.model.Zanr;
import server.repository.PlocaRepository;
import server.repository.ProizvodRepository;
import server.repository.ZanrRepository;

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
    
    @Autowired
    @Lazy
    private ProizvodRepository proizvodRepository;
    
    @Autowired
    @Lazy
    private ZanrRepository zanrRepository;

    @Override
    protected CrudRepository<Ploca, Long> getRepository() {
        return plocaRepository;
    }

    @Override
    public PlocaDTO convertToDTO(Ploca entity) {
        return new PlocaDTO(
            entity.getId(),
            proizvodService.convertToDTO(entity.getProizvod()),
            entity.getListaPesama(),
            entity.getBrend(),
            entity.getIzdavackaKuca(),
            entity.getGodinaIzdanja(),
            new ZanrDTO(entity.getZanr().getId(), entity.getZanr().getNaziv(),null,entity.getZanr().getVidljiv()),
            entity.getVidljiv()
        );
    }

    @Override
    protected Ploca convertToEntity(PlocaDTO dto) {
        Ploca ploca = new Ploca();


        Proizvod proizvod;
        if (dto.getProizvod().getId() != null) {
   
            proizvod = proizvodRepository.findById(dto.getProizvod().getId())
                .orElseThrow(() -> new RuntimeException("Proizvod sa ID " + dto.getProizvod().getId() + " ne postoji"));
        } else {

            proizvod = proizvodService.convertToEntity(dto.getProizvod());
            proizvod = proizvodRepository.save(proizvod); 
        }


        ploca.setProizvod(proizvod);
        ploca.setId(proizvod.getId()); 
        

        ploca.setListaPesama(dto.getListaPesama());
        ploca.setBrend(dto.getBrend());
        ploca.setIzdavackaKuca(dto.getIzdavackaKuca());
        ploca.setGodinaIzdanja(dto.getGodinaIzdanja());


        if (dto.getZanr() != null && dto.getZanr().getId() != null) {
            Zanr zanr = new Zanr();
            zanr.setId(dto.getZanr().getId());
            ploca.setZanr(zanr);
        } else {
            ploca.setZanr(null);
        }

        ploca.setVidljiv(dto.getVidljiv());

        return ploca;
    }


    @Override
    protected void updateEntityFromDto(PlocaDTO dto, Ploca entity) {

        if (dto.getProizvod() != null) {
            Proizvod proizvod = entity.getProizvod(); 

            proizvod.setNaziv(dto.getProizvod().getNaziv());
            proizvod.setCena(dto.getProizvod().getCena());
            proizvod.setOpis(dto.getProizvod().getOpis());
            proizvod.setSlikaPutanja(dto.getProizvod().getSlikaPutanja());
            proizvod.setVidljiv(dto.getProizvod().getVidljiv());

            proizvodRepository.save(proizvod); 
        }


        entity.setListaPesama(dto.getListaPesama());
        entity.setBrend(dto.getBrend());
        entity.setIzdavackaKuca(dto.getIzdavackaKuca());
        entity.setGodinaIzdanja(dto.getGodinaIzdanja());

        if (dto.getZanr() != null && dto.getZanr().getId() != null) {
            Zanr zanr = new Zanr();
            zanr.setId(dto.getZanr().getId());
            entity.setZanr(zanr);
        } else {
            entity.setZanr(null);
        }

        entity.setVidljiv(dto.getVidljiv());
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
    
    @Override
    @Transactional
    public PlocaDTO save(PlocaDTO dto) {
     
        Proizvod proizvod = proizvodService.convertToEntity(dto.getProizvod());


        proizvod = proizvodRepository.save(proizvod);

 
        Ploca ploca = new Ploca();
        ploca.setProizvod(proizvod); 
        ploca.setListaPesama(dto.getListaPesama());
        ploca.setBrend(dto.getBrend());
        ploca.setIzdavackaKuca(dto.getIzdavackaKuca());
        ploca.setGodinaIzdanja(dto.getGodinaIzdanja());
        ploca.setZanr(zanrRepository.findById(dto.getZanr().getId()).orElseThrow());
        ploca.setVidljiv(true);

        return convertToDTO(plocaRepository.save(ploca));
    }

    public List<Ploca> findByZanrId(Long id) {
        return plocaRepository.findByZanrId(id);
    }


}  

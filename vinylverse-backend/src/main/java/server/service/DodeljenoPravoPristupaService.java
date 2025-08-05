package server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import server.DTOs.DodeljenoPravoPristupaDTO;
import server.model.DodeljenoPravoPristupa;
import server.model.Korisnik;
import server.model.PravoPristupa;
import server.repository.DodeljenoPravoPristupaRepository;

@Service
public class DodeljenoPravoPristupaService extends BaseService<DodeljenoPravoPristupa, DodeljenoPravoPristupaDTO, Long> {

    @Autowired
    private DodeljenoPravoPristupaRepository dodeljenoRepo;

    @Autowired
    @Lazy
    private KorisnikService korisnikService;

    @Autowired
    @Lazy
    private PravoPristupaService pravoPristupaService;

    @Override
    protected CrudRepository<DodeljenoPravoPristupa, Long> getRepository() {
        return dodeljenoRepo;
    }

    @Override
    protected DodeljenoPravoPristupaDTO convertToDTO(DodeljenoPravoPristupa entity) {
        return new DodeljenoPravoPristupaDTO(
            entity.getId(),
            null, 
            pravoPristupaService.convertToDTO(entity.getPravoPristupa()),
            entity.getVidljiv()
        );
    }

    @Override
    protected DodeljenoPravoPristupa convertToEntity(DodeljenoPravoPristupaDTO dto) {
        DodeljenoPravoPristupa dp = new DodeljenoPravoPristupa();
        dp.setId(dto.getId());
        dp.setVidljiv(true);

        if (dto.getPravoPristupa() != null && dto.getPravoPristupa().getId() != null) {
            PravoPristupa pp = new PravoPristupa();
            pp.setId(dto.getPravoPristupa().getId());
            dp.setPravoPristupa(pp);
        }

        if (dto.getKorisnik() != null && dto.getKorisnik().getId() != null) {
            Korisnik k = new Korisnik();
            k.setId(dto.getKorisnik().getId());
            dp.setKorisnik(k);
        }

        return dp;
    }

    @Override
    protected void updateEntityFromDto(DodeljenoPravoPristupaDTO dto, DodeljenoPravoPristupa entity) {
        entity.setVidljiv(dto.getVidljiv());

        if (dto.getPravoPristupa() != null && dto.getPravoPristupa().getId() != null) {
            PravoPristupa pravo = new PravoPristupa();
            pravo.setId(dto.getPravoPristupa().getId());
            entity.setPravoPristupa(pravo);
        }

        if (dto.getKorisnik() != null && dto.getKorisnik().getId() != null) {
            Korisnik korisnik = new Korisnik();
            korisnik.setId(dto.getKorisnik().getId());
            entity.setKorisnik(korisnik);
        }
    }
}

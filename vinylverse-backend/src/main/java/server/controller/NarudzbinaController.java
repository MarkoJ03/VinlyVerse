package server.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import server.DTOs.NarudzbinaDTO;
import server.model.Narudzbina;
import server.service.NarudzbinaService;

@Controller
@RequestMapping("/api/narudzbina")
public class NarudzbinaController extends BaseController<Narudzbina, NarudzbinaDTO, Long> {

    @Autowired
    private NarudzbinaService narudzbinaService;

    @Override
    protected NarudzbinaService getService() {
        return narudzbinaService;
    }

    @GetMapping("/moje")
    public ResponseEntity<List<NarudzbinaDTO>> mojeNarudzbine(Principal principal) {
        if (principal == null || principal.getName() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(narudzbinaService.findMojeNarudzbineByEmail(principal.getName()));
    }

    @Override
    @PostMapping
    public ResponseEntity<NarudzbinaDTO> add(@RequestBody NarudzbinaDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        NarudzbinaDTO saved = narudzbinaService.saveForAuthenticatedUser(authentication.getName(), dto);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }
}

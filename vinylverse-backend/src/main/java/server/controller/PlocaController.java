package server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import server.DTOs.PlocaDTO;
import server.model.Ploca;
import server.service.PlocaService;

@Controller
@RequestMapping("/api/ploca")

public class PlocaController extends BaseController<Ploca, PlocaDTO, Long> {

    @Autowired
    private PlocaService plocaService;

    @Override
    protected PlocaService getService() {
        return plocaService;
    }
    @GetMapping("/paginacija")
    public ResponseEntity<List<PlocaDTO>> getPaginirane(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        return ResponseEntity.ok(plocaService.getPaginiranePloce(page, size));
    }

    @GetMapping("/nasumicno")
    public ResponseEntity<List<PlocaDTO>> getNasumicno(@RequestParam(defaultValue = "5") int broj) {
        return ResponseEntity.ok(plocaService.getNasumicnePloce(broj));
    }
}


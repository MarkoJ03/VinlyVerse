package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import server.DTOs.StavkaNarudzbineDTO;
import server.model.StavkaNarudzbine;
import server.service.StavkaNarudzbineService;

@Controller
@RequestMapping("/api/stavka-narudzbine")
public class StavkaNarudzbineController extends BaseController<StavkaNarudzbine, StavkaNarudzbineDTO, Long> {

    @Autowired
    private StavkaNarudzbineService stavkaNarudzbineService;

    @Override
    protected StavkaNarudzbineService getService() {
        return stavkaNarudzbineService;
    }
}

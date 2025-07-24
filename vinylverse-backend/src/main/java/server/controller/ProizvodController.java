package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import server.DTOs.ProizvodDTO;
import server.model.Proizvod;

import server.service.ProizvodService;

@Controller
@RequestMapping("/api/proizvod")

public class ProizvodController extends BaseController<Proizvod, ProizvodDTO, Long> {

    @Autowired
    private ProizvodService ProizvodService;

    @Override
    protected ProizvodService getService() {
        return ProizvodService;
    }

}
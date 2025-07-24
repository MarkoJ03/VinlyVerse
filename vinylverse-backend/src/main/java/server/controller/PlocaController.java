package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import server.DTOs.PlocaDTO;
import server.model.Ploca;
import server.service.PlocaService;

@Controller
@RequestMapping("/api/ploca")

public class PlocaController extends BaseController<Ploca, PlocaDTO, Long> {

    @Autowired
    private PlocaService PlocaService;

    @Override
    protected PlocaService getService() {
        return PlocaService;
    }

}

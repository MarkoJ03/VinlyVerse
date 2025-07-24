package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import server.DTOs.PravoPristupaDTO;
import server.model.PravoPristupa;
import server.service.PravoPristupaService;

@Controller
@RequestMapping("/api/pravoPristupa")

public class PravoPristupaController extends BaseController<PravoPristupa, PravoPristupaDTO, Long> {

    @Autowired
    private PravoPristupaService PravoPristupaService;

    @Override
    protected PravoPristupaService getService() {
        return PravoPristupaService;
    }

}

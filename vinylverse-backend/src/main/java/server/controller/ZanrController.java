package server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import server.DTOs.ZanrDTO;
import server.model.Zanr;

import server.service.ZanrService;

@Controller
@RequestMapping("/api/zanr")

public class ZanrController extends BaseController<Zanr, ZanrDTO, Long> {

    @Autowired
    private ZanrService ZanrService;

    @Override
    protected ZanrService getService() {
        return ZanrService;
    }

}
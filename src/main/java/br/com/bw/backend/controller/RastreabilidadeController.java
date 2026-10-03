package br.com.bw.backend.controller;

import br.com.bw.backend.service.RastreabilidadeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/rastreabilidades")
public class RastreabilidadeController {
    private final RastreabilidadeService rastreabilidadeService;

    public RastreabilidadeController(RastreabilidadeService rastreabilidadeService) {
        this.rastreabilidadeService = rastreabilidadeService;
    }
}

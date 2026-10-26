package br.com.bw.backend.controller;

import br.com.bw.backend.service.FluxoFinanceiroService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/fluxo-financeiros")
public class FluxoFinanceiroController {
    private final FluxoFinanceiroService fluxoFinanceiroService;

    public FluxoFinanceiroController(FluxoFinanceiroService fluxoFinanceiroService) {
        this.fluxoFinanceiroService = fluxoFinanceiroService;
    }
}

package br.com.bw.backend.controller;

import br.com.bw.backend.service.FinanceiroService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/financeiros")
public class FinanceiroController {
    private final FinanceiroService financeiroService;

    public FinanceiroController(FinanceiroService financeiroService) {
        this.financeiroService = financeiroService;
    }
}

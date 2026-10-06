package br.com.bw.backend.service;

import org.springframework.stereotype.Service;

@Service
public class FinanceiroService {
    private final FinanceiroService financeiroService;

    public FinanceiroService(FinanceiroService financeiroService) {
        this.financeiroService = financeiroService;
    }
}

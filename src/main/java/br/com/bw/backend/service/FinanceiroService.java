package br.com.bw.backend.service;

import br.com.bw.backend.repository.FinanceiroRepository;
import org.springframework.stereotype.Service;

@Service
public class FinanceiroService {
    private final FinanceiroRepository financeiroRepository;

    public FinanceiroService(FinanceiroRepository financeiroRepository) {
        this.financeiroRepository = financeiroRepository;
    }
}

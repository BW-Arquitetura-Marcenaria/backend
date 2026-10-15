package br.com.bw.backend.service;

import br.com.bw.backend.repository.FinanceiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FinanceiroService {
    private final FinanceiroRepository financeiroRepository;
}

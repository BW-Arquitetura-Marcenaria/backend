package br.com.bw.backend.service;

import br.com.bw.backend.repository.FluxoFinanceiroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FluxoFinanceiroService {
    private final FluxoFinanceiroRepository fluxoFinanceiroRepository;
}

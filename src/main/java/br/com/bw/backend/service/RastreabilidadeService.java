package br.com.bw.backend.service;

import br.com.bw.backend.repository.RastreabilidadeRepository;
import org.springframework.stereotype.Service;

@Service
public class RastreabilidadeService {
    private final RastreabilidadeRepository rastreabilidadeRepository;

    public RastreabilidadeService(RastreabilidadeRepository rastreabilidadeRepository) {
        this.rastreabilidadeRepository = rastreabilidadeRepository;
    }
}

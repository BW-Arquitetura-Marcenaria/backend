package br.com.bw.backend.service;

import br.com.bw.backend.repository.ReferenciaRepository;
import org.springframework.stereotype.Service;

@Service
public class ReferenciaService {
    private final ReferenciaRepository referenciaRepository;

    public ReferenciaService(ReferenciaRepository referenciaRepository) {
        this.referenciaRepository = referenciaRepository;
    }
}

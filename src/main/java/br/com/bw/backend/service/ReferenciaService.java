package br.com.bw.backend.service;

import br.com.bw.backend.entity.Referencia;
import br.com.bw.backend.repository.ReferenciaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReferenciaService {
    private final ReferenciaRepository referenciaRepository;

    public ReferenciaService(ReferenciaRepository referenciaRepository) {
        this.referenciaRepository = referenciaRepository;
    }

    public Page<Referencia> listAll(Pageable pageable) {
        return referenciaRepository.findAll(pageable);
    }
}

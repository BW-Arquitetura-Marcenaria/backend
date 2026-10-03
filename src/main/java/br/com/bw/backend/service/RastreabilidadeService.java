package br.com.bw.backend.service;

import br.com.bw.backend.entity.Rastreabilidade;
import br.com.bw.backend.repository.RastreabilidadeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RastreabilidadeService {
    private final RastreabilidadeRepository rastreabilidadeRepository;

    public RastreabilidadeService(RastreabilidadeRepository rastreabilidadeRepository) {
        this.rastreabilidadeRepository = rastreabilidadeRepository;
    }

    public Page<Rastreabilidade> findAll(Pageable pageable) {
        return rastreabilidadeRepository.findAll(pageable);
    }
}

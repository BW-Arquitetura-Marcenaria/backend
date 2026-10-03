package br.com.bw.backend.service;

import br.com.bw.backend.entity.Rastreabilidade;
import br.com.bw.backend.repository.ProjetoRepository;
import br.com.bw.backend.repository.RastreabilidadeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RastreabilidadeService {
    private final RastreabilidadeRepository rastreabilidadeRepository;
    private final ProjetoRepository projetoRepository;

    public RastreabilidadeService(RastreabilidadeRepository rastreabilidadeRepository, ProjetoRepository projetoRepository) {
        this.rastreabilidadeRepository = rastreabilidadeRepository;
        this.projetoRepository = projetoRepository;
    }

    public Page<Rastreabilidade> findAll(Pageable pageable) {
        return rastreabilidadeRepository.findAll(pageable);
    }

    public Rastreabilidade save(Rastreabilidade rastreabilidade) {
        if (validationRastreabilidadeSave(rastreabilidade)) {
            throw new IllegalArgumentException("Rastreabilidade incorreta");
        }

        return rastreabilidadeRepository.save(rastreabilidade);
    }

    public boolean validationRastreabilidadeSave(Rastreabilidade rastreabilidade) {
        if (rastreabilidade.getProjeto() == null || rastreabilidade.getProjeto().getId() == null) {
            return false;
        }

        return projetoRepository.existsById(rastreabilidade.getProjeto().getId());
    }
}

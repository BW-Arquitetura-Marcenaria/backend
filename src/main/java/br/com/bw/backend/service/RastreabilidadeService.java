package br.com.bw.backend.service;

import br.com.bw.backend.entity.Rastreabilidade;
import br.com.bw.backend.repository.ProjetoRepository;
import br.com.bw.backend.repository.RastreabilidadeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public Rastreabilidade update(Integer id, Rastreabilidade rastreabilidade) {
        var rastreabilidadeExistente = rastreabilidadeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rastreabilidade não encontrada"));

        if (validationRastreabilidadeSave(rastreabilidade)) {
            throw new IllegalArgumentException("Rastreabilidade incorreta");
        }

        rastreabilidade.setId(rastreabilidadeExistente.getId());
        return rastreabilidadeRepository.save(rastreabilidade);
    }

    public void delete(Integer id) {
        if (!rastreabilidadeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rastreabilidade não encontrada");
        }

        rastreabilidadeRepository.deleteById(id);
    }

    public boolean validationRastreabilidadeSave(Rastreabilidade rastreabilidade) {
        if (rastreabilidade.getProjeto() == null || rastreabilidade.getProjeto().getId() == null) {
            return true;
        }

        return !projetoRepository.existsById(rastreabilidade.getProjeto().getId());
    }
}

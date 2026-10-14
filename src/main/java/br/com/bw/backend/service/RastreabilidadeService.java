package br.com.bw.backend.service;

import br.com.bw.backend.entity.Rastreabilidade;
import br.com.bw.backend.exception.BusinessValidationException;
import br.com.bw.backend.exception.NotFoundException;
import br.com.bw.backend.repository.ProjetoRepository;
import br.com.bw.backend.repository.RastreabilidadeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RastreabilidadeService {
    private final RastreabilidadeRepository rastreabilidadeRepository;
    private final ProjetoRepository projetoRepository;

    public Page<Rastreabilidade> findAll(Pageable pageable) {
        log.info("Listando rastreabilidade - página: {}, tamanho: {}", pageable.getPageNumber(), pageable.getPageSize());
        return rastreabilidadeRepository.findAll(pageable);
    }

    public Rastreabilidade save(Rastreabilidade rastreabilidade) {
        if (validationRastreabilidadeSave(rastreabilidade)) {
            log.warn("Rastreabilidade incorreta - id: {}", rastreabilidade.getId());
            throw new BusinessValidationException("Rastreabilidade incorreta");
        }

        log.info("Rastreabilidade criada com sucesso - id: {}", rastreabilidade.getId());
        return rastreabilidadeRepository.save(rastreabilidade);
    }

    @Transactional
    public Rastreabilidade update(Integer id, Rastreabilidade rastreabilidade) {
        log.info("Atualizando rastreabilidade - id: {}", id);

        var rastreabilidadeExistente = rastreabilidadeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Referência não encontrada para atualização - id: {}", id);
                    return new NotFoundException("Rastreabilidade não encontrada");
                });

        if (validationRastreabilidadeSave(rastreabilidade)) {
            log.warn("Referência incorreta - id: {}", id);
            throw new BusinessValidationException("Rastreabilidade incorreta");
        }

        rastreabilidade.setId(rastreabilidadeExistente.getId());
        log.info("Rastreabilidade atualizada com sucesso - id: {}", rastreabilidade.getId());
        return rastreabilidadeRepository.save(rastreabilidade);
    }

    public void delete(Integer id) {
        log.info("Removendo rastreabilidade - id: {}", id);

        if (!rastreabilidadeRepository.existsById(id)) {
            log.warn("Rastreabilidade não encontrada para atualização - id: {}", id);
            throw new NotFoundException("Rastreabilidade não encontrada");
        }

        rastreabilidadeRepository.deleteById(id);
        log.info("Rastreabilidade removida com sucesso - id: {}", id);
    }

    public boolean validationRastreabilidadeSave(Rastreabilidade rastreabilidade) {
        if (rastreabilidade.getProjeto() == null || rastreabilidade.getProjeto().getId() == null) {
            return true;
        }

        return !projetoRepository.existsById(rastreabilidade.getProjeto().getId());
    }
}

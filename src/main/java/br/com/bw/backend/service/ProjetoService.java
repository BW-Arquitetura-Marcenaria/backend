package br.com.bw.backend.service;

import br.com.bw.backend.entity.Projeto;
import br.com.bw.backend.entity.enums.StatusProjeto;
import br.com.bw.backend.exception.NotFoundException;
import br.com.bw.backend.repository.ProjetoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProjetoService {
    private final ProjetoRepository projetoRepository;

    public ProjetoService(ProjetoRepository projetoRepository) {
        this.projetoRepository = projetoRepository;
    }

    public Page<Projeto> findAll(Pageable pageable) {
        log.info("Listando projetos - página: {}, tamanho: {}", pageable.getPageNumber(), pageable.getPageSize());
        return projetoRepository.findAll(pageable);
    }

    public Projeto findById(Integer id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Projeto não encontrado - id: {}", id);
                    return new NotFoundException("Projeto não encontrado");
                });
    }

    public Projeto save(Projeto projeto) {
        if (projeto.getStatus() == null) {
            projeto.setStatus(StatusProjeto.PLANEJAMENTO);
        }

        var projetoSalvo = projetoRepository.save(projeto);
        log.info("Projeto criado com sucesso - id: {}", projetoSalvo.getId());
        return projetoSalvo;
    }

    public Projeto update(Integer id, Projeto projeto) {
        log.info("Atualizando projeto - id: {}", id);

        var projetoExistente = findById(id);

        if (projeto.getStatus() == null) {
            projeto.setStatus(StatusProjeto.PLANEJAMENTO);
        }

        projeto.setId(projetoExistente.getId());
        var projetoAtualizado = projetoRepository.save(projeto);
        log.info("Projeto atualizado com sucesso - id: {}", projetoAtualizado.getId());
        return projetoAtualizado;
    }

    public void delete(Integer id) {
        log.info("Removendo projeto - id: {}", id);

        if (!projetoRepository.existsById(id)) {
            log.warn("Projeto não encontrado para remoção - id: {}", id);
            throw new NotFoundException("Projeto não encontrado");
        }

        projetoRepository.deleteById(id);
        log.info("Projeto removido com sucesso - id: {}", id);
    }
}

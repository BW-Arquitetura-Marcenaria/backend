package br.com.bw.backend.service;

import br.com.bw.backend.entity.Projeto;
import br.com.bw.backend.entity.enums.StatusProjeto;
import br.com.bw.backend.repository.ProjetoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProjetoService {
    private final ProjetoRepository projetoRepository;

    public ProjetoService(ProjetoRepository projetoRepository) {
        this.projetoRepository = projetoRepository;
    }

    public Page<Projeto> findAll(Pageable pageable) {
        return projetoRepository.findAll(pageable);
    }

    public Projeto findById(Integer id) {
        return projetoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));
    }

    public Projeto save(Projeto projeto) {
        if (projeto.getStatus() == null) {
            projeto.setStatus(StatusProjeto.PLANEJAMENTO);
        }

        return projetoRepository.save(projeto);
    }

    public Projeto update(Integer id, Projeto projeto) {
        var projetoExistente = findById(id);

        if (projeto.getStatus() == null) {
            projeto.setStatus(StatusProjeto.PLANEJAMENTO);
        }

        projeto.setId(projetoExistente.getId());
        return projetoRepository.save(projeto);
    }

    public void delete(Integer id) {
        if (!projetoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado");
        }

        projetoRepository.deleteById(id);
    }
}

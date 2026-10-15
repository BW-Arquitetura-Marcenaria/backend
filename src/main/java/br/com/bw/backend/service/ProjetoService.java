package br.com.bw.backend.service;

import br.com.bw.backend.entity.Projeto;
import br.com.bw.backend.entity.enums.StatusProjeto;
import br.com.bw.backend.exception.NotFoundException;
import br.com.bw.backend.repository.ProjetoRepository;
import br.com.bw.backend.repository.UsuarioRepository;
import br.com.bw.backend.security.UsuarioLogado;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjetoService {
    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;

    public Page<Projeto> findAll(Pageable pageable) {
        log.info("Listando projetos - página: {}, tamanho: {}", pageable.getPageNumber(), pageable.getPageSize());
        if (UsuarioLogado.isAdmin()) {
            return projetoRepository.findAll(pageable);
        }
        return projetoRepository.findByClienteId(UsuarioLogado.id(), pageable);
    }

    public Projeto findById(Integer id) {
        var projeto = UsuarioLogado.isAdmin()
                ? projetoRepository.findById(id)
                : projetoRepository.findByIdAndClienteId(id, UsuarioLogado.id());

        return projeto.orElseThrow(() ->
                new NotFoundException("Projeto não encontrado"));
    }

    public Projeto save(Projeto projeto, Integer clienteId) {
        var cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new NotFoundException("Cliente não encontrado"));

        projeto.setCliente(cliente);

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

        projeto.setCliente(projetoExistente.getCliente());

        return projetoRepository.save(projeto);
    }

    public void delete(Integer id) {
        log.info("Removendo projeto - id: {}", id);

        var projeto = findById(id);
        projetoRepository.delete(projeto);
        log.info("Projeto removido com sucesso - id: {}", id);
    }
}
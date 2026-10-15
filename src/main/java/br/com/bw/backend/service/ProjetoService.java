package br.com.bw.backend.service;

import br.com.bw.backend.entity.Projeto;
import br.com.bw.backend.entity.enums.StatusProjeto;
import br.com.bw.backend.repository.ProjetoRepository;
import br.com.bw.backend.repository.UsuarioRepository;
import br.com.bw.backend.security.UsuarioLogado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProjetoService {
    private final ProjetoRepository projetoRepository;
    private final UsuarioRepository usuarioRepository;

    public ProjetoService(ProjetoRepository projetoRepository, UsuarioRepository usuarioRepository) {
        this.projetoRepository = projetoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Page<Projeto> findAll(Pageable pageable) {
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
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Projeto não encontrado"));
    }

    public Projeto save(Projeto projeto, Integer clienteId) {
        var cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

        projeto.setCliente(cliente);

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

        projeto.setCliente(projetoExistente.getCliente());

        return projetoRepository.save(projeto);
    }

    public void delete(Integer id) {
        var projeto = findById(id);

        projetoRepository.delete(projeto);
    }
}
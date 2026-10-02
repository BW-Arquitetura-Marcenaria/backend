package br.com.bw.backend.service;

import br.com.bw.backend.repository.ProjetoRepository;
import org.springframework.stereotype.Service;

@Service
public class ProjetoService {
    private final ProjetoRepository projetoRepository;

    public ProjetoService(ProjetoRepository projetoRepository) {
        this.projetoRepository = projetoRepository;
    }
}

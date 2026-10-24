package br.com.bw.backend.service;

import br.com.bw.backend.dto.response.ClienteInstagramGetResponseDTO;
import br.com.bw.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteIntegracaoService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public ClienteInstagramGetResponseDTO consultarPorInstagram(String instagramId) {
        return new ClienteInstagramGetResponseDTO(usuarioRepository.existsByInstagramId(instagramId));
    }
}
package br.com.bw.backend.controller;

import br.com.bw.backend.dto.response.ClienteInstagramGetResponseDTO;
import br.com.bw.backend.service.ClienteIntegracaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/integracoes/clientes")
@RequiredArgsConstructor
public class ClienteIntegracaoController {

    private final ClienteIntegracaoService service;

    @GetMapping("/instagram/{instagramId}")
    public ClienteInstagramGetResponseDTO consultarPorInstagram(@PathVariable String instagramId) {
        return service.consultarPorInstagram(instagramId);
    }
}
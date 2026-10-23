package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.ConviteAdminAceitarPostRequestDTO;
import br.com.bw.backend.dto.request.ConviteAdminRecusarPostRequestDTO;
import br.com.bw.backend.dto.response.ConviteAdminValidacaoGetResponseDTO;
import br.com.bw.backend.service.ConviteAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/convite-admin")
@RequiredArgsConstructor
public class ConviteAdminController {

    private final ConviteAdminService service;

    @GetMapping("/validar")
    public ConviteAdminValidacaoGetResponseDTO validar(@RequestParam String token) {
        return service.validar(token);
    }

    @PostMapping("/aceitar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void aceitar(@RequestBody @Valid ConviteAdminAceitarPostRequestDTO dto) {
        service.aceitar(dto);
    }

    @PostMapping("/recusar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void recusar(@RequestBody @Valid ConviteAdminRecusarPostRequestDTO dto) {
        service.recusar(dto);
    }
}
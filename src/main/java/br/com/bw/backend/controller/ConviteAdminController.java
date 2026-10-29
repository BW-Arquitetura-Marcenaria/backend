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
    public ConviteAdminValidacaoGetResponseDTO validate(@RequestParam String token) {
        return service.validate(token);
    }

    @PostMapping("/aceitar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void accept(@RequestBody @Valid ConviteAdminAceitarPostRequestDTO dto) {
        service.accept(dto);
    }

    @PostMapping("/recusar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@RequestBody @Valid ConviteAdminRecusarPostRequestDTO dto) {
        service.reject(dto);
    }
}
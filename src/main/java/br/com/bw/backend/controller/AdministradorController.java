package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.ConviteAdminPostRequestDTO;
import br.com.bw.backend.dto.response.AdministradoresGetResponseDTO;
import br.com.bw.backend.dto.response.AdministradoresGetResponseDTO.AdministradorItem;
import br.com.bw.backend.service.ConviteAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/administradores")
@RequiredArgsConstructor
public class AdministradorController {

    private final ConviteAdminService service;

    @GetMapping
    public AdministradoresGetResponseDTO listar(@AuthenticationPrincipal Integer usuarioId) {
        return service.listar(usuarioId);
    }

    @PostMapping("/convites")
    @ResponseStatus(HttpStatus.CREATED)
    public AdministradorItem convidar(@AuthenticationPrincipal Integer usuarioId,
                                      @RequestBody @Valid ConviteAdminPostRequestDTO dto) {
        return service.convidar(usuarioId, dto);
    }

    @PutMapping("/{id}/desativar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@AuthenticationPrincipal Integer usuarioId,
                          @PathVariable Integer id) {
        service.desativar(usuarioId, id);
    }
}
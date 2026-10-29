package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.ContaAdminEmpresaPutRequestDTO;
import br.com.bw.backend.dto.request.ContaAdminEnderecoPutRequestDTO;
import br.com.bw.backend.dto.request.ContaAdminSegurancaPutRequestDTO;
import br.com.bw.backend.dto.response.ContaAdminGetResponseDTO;
import br.com.bw.backend.service.ContaAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/conta")
@RequiredArgsConstructor
public class ContaAdminController {

    private final ContaAdminService contaAdminService;

    @GetMapping
    public ResponseEntity<ContaAdminGetResponseDTO> find() {
        return ResponseEntity.status(HttpStatus.OK).body(contaAdminService.find());
    }

    @PutMapping("/empresa")
    public ResponseEntity<ContaAdminGetResponseDTO> updateCompany(
            @Valid @RequestBody ContaAdminEmpresaPutRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(contaAdminService.updateCompany(dto));
    }

    @PutMapping("/endereco")
    public ResponseEntity<ContaAdminGetResponseDTO> updateAddress(
            @Valid @RequestBody ContaAdminEnderecoPutRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(contaAdminService.updateAddress(dto));
    }

    @PutMapping("/seguranca")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ContaAdminSegurancaPutRequestDTO dto) {
        contaAdminService.changePassword(dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
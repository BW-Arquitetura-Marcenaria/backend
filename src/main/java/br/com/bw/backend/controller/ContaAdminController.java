package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.ContaAdminEmpresaPutRequestDTO;
import br.com.bw.backend.dto.request.ContaAdminEnderecoPutRequestDTO;
import br.com.bw.backend.dto.request.ContaAdminSegurancaPutRequestDTO;
import br.com.bw.backend.dto.response.ContaAdminGetResponseDTO;
import br.com.bw.backend.service.ContaAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Conta do Administrador", description = "Dados da conta, empresa, endereço e segurança do administrador logado")
@SecurityRequirement(name = "bearerAuth")
public class ContaAdminController {

    private final ContaAdminService contaAdminService;

    @GetMapping
    @Operation(
            summary = "Consultar conta do administrador logado",
            description = "Retorna os dados pessoais, da empresa e do endereço do administrador autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador")
    })
    public ResponseEntity<ContaAdminGetResponseDTO> find() {
        return ResponseEntity.status(HttpStatus.OK).body(contaAdminService.find());
    }

    @PutMapping("/empresa")
    @Operation(
            summary = "Atualizar dados da empresa",
            description = "Atualiza razão social, nome fantasia e demais dados da empresa vinculada ao administrador logado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados da empresa atualizados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador")
    })
    public ResponseEntity<ContaAdminGetResponseDTO> updateCompany(
            @Valid @RequestBody ContaAdminEmpresaPutRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(contaAdminService.updateCompany(dto));
    }

    @PutMapping("/endereco")
    @Operation(
            summary = "Atualizar endereço da empresa",
            description = "Atualiza o endereço associado à empresa do administrador logado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Endereço atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador")
    })
    public ResponseEntity<ContaAdminGetResponseDTO> updateAddress(
            @Valid @RequestBody ContaAdminEnderecoPutRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.OK).body(contaAdminService.updateAddress(dto));
    }

    @PutMapping("/seguranca")
    @Operation(
            summary = "Alterar senha",
            description = "Altera a senha de acesso do administrador logado, mediante confirmação da senha atual.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou nova senha inválida"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador")
    })
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ContaAdminSegurancaPutRequestDTO dto) {
        contaAdminService.changePassword(dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
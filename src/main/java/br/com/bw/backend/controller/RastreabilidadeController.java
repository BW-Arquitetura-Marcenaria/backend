package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.RastreabilidadePostRequestDTO;
import br.com.bw.backend.dto.request.RastreabilidadePutRequestDTO;
import br.com.bw.backend.dto.response.RastreabilidadeGetResponseDTO;
import br.com.bw.backend.mapper.RastreabilidadeMapper;
import br.com.bw.backend.service.RastreabilidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/rastreabilidades")
@Tag(name = "Rastreabilidade da Madeira", description = "Registro de rastreabilidade da madeira utilizada nos projetos (apenas ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class RastreabilidadeController {
    private final RastreabilidadeService rastreabilidadeService;
    private final RastreabilidadeMapper mapper;

    public RastreabilidadeController(RastreabilidadeService rastreabilidadeService, RastreabilidadeMapper mapper) {
        this.rastreabilidadeService = rastreabilidadeService;
        this.mapper = mapper;
    }

    @GetMapping()
    @Operation(
            summary = "Listar registros de rastreabilidade",
            description = "Lista todos os registros de rastreabilidade da madeira, de forma paginada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador")
    })
    public ResponseEntity<Page<RastreabilidadeGetResponseDTO>> findAll(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        var rastreabilidadeFindAll = rastreabilidadeService.findAll(pageable);
        var response = mapper.toRastreabilidadeGetResponseDTO(rastreabilidadeFindAll);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping()
    @Operation(
            summary = "Criar registro de rastreabilidade",
            description = "Cadastra um novo registro de rastreabilidade de madeira vinculado a um projeto.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registro criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador")
    })
    public ResponseEntity<RastreabilidadeGetResponseDTO> save(@RequestBody @Valid RastreabilidadePostRequestDTO rastreabilidadePostRequestDTO) {
        var rastreabilidadeSaved = rastreabilidadeService.save(mapper.toRastreabilidade(rastreabilidadePostRequestDTO));
        var response = mapper.toRastreabilidadeGetResponseDTO(rastreabilidadeSaved);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar registro de rastreabilidade",
            description = "Atualiza os dados de um registro de rastreabilidade existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    public ResponseEntity<RastreabilidadeGetResponseDTO> update(
            @Parameter(description = "Identificador do registro de rastreabilidade", required = true)
            @PathVariable Integer id,
            @RequestBody @Valid RastreabilidadePutRequestDTO rastreabilidadePutRequestDTO
    ) {
        var rastreabilidadeUpdated = rastreabilidadeService.update(id, mapper.toRastreabilidade(rastreabilidadePutRequestDTO));
        var response = mapper.toRastreabilidadeGetResponseDTO(rastreabilidadeUpdated);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Excluir registro de rastreabilidade",
            description = "Remove um registro de rastreabilidade existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Registro excluído com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador"),
            @ApiResponse(responseCode = "404", description = "Registro não encontrado")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identificador do registro de rastreabilidade", required = true)
            @PathVariable Integer id) {
        rastreabilidadeService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("projeto/{projetoId}")
    @Operation(
            summary = "Listar rastreabilidade por projeto",
            description = "Lista os registros de rastreabilidade de madeira vinculados a um projeto específico, de forma paginada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    public ResponseEntity<Page<RastreabilidadeGetResponseDTO>> listAllByProjeto(
            @Parameter(description = "Identificador do projeto", required = true)
            @PathVariable Integer projetoId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        var response = rastreabilidadeService.listAllByProjeto(projetoId, pageable)
                .map(mapper::toRastreabilidadeGetResponseDTO);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

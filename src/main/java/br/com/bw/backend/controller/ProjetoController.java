package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.ProjetoPostRequestDTO;
import br.com.bw.backend.dto.request.ProjetoPutRequestDTO;
import br.com.bw.backend.dto.response.ProjetoGetResponseDTO;
import br.com.bw.backend.mapper.ProjetoMapper;
import br.com.bw.backend.service.ProjetoService;
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
@RequestMapping("v1/projetos")
@Tag(name = "Projetos", description = "Gestão de projetos de marcenaria dos clientes")
@SecurityRequirement(name = "bearerAuth")
public class ProjetoController {
    private final ProjetoService projetoService;
    private final ProjetoMapper mapper;

    public ProjetoController(ProjetoService projetoService, ProjetoMapper mapper) {
        this.projetoService = projetoService;
        this.mapper = mapper;
    }

    @GetMapping()
    @Operation(
            summary = "Listar projetos",
            description = "Lista os projetos cadastrados de forma paginada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    public ResponseEntity<Page<ProjetoGetResponseDTO>> findAll(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        var projetoFindAll = projetoService.findAll(pageable);
        var response = mapper.toProjetoGetResponseDTO(projetoFindAll);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar projeto por id",
            description = "Retorna os dados de um projeto específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projeto encontrado"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    public ResponseEntity<ProjetoGetResponseDTO> findById(
            @Parameter(description = "Identificador do projeto", required = true)
            @PathVariable Integer id) {
        var projeto = projetoService.findById(id);
        var response = mapper.toProjetoGetResponseDTO(projeto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping()
    @Operation(
            summary = "Criar projeto",
            description = "Cadastra um novo projeto vinculado a um cliente. Requer perfil ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Projeto criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador")
    })
    public ResponseEntity<ProjetoGetResponseDTO> save(@RequestBody @Valid ProjetoPostRequestDTO projetoPostRequestDTO) {
        var projetoSaved = projetoService.save(
                mapper.toProjeto(projetoPostRequestDTO),
                projetoPostRequestDTO.clienteId());
        var response = mapper.toProjetoGetResponseDTO(projetoSaved);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar projeto",
            description = "Atualiza os dados de um projeto existente. Requer perfil ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projeto atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    public ResponseEntity<ProjetoGetResponseDTO> update(
            @Parameter(description = "Identificador do projeto", required = true)
            @PathVariable Integer id,
            @RequestBody @Valid ProjetoPutRequestDTO projetoPutRequestDTO
    ) {
        var projetoUpdated = projetoService.update(id, mapper.toProjeto(projetoPutRequestDTO));
        var response = mapper.toProjetoGetResponseDTO(projetoUpdated);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Excluir projeto",
            description = "Remove um projeto existente. Requer perfil ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Projeto excluído com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "403", description = "Usuário não é administrador"),
            @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identificador do projeto", required = true)
            @PathVariable Integer id) {
        projetoService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

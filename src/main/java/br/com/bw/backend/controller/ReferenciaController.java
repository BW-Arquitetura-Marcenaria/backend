package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.ReferenciaPostRequestDTO;
import br.com.bw.backend.dto.request.ReferenciaPutRequestDTO;
import br.com.bw.backend.dto.response.ReferenciaGetResponseDTO;
import br.com.bw.backend.mapper.ReferenciaMapper;
import br.com.bw.backend.service.ReferenciaExcelExportService;
import br.com.bw.backend.service.ReferenciaPdfExportService;
import br.com.bw.backend.service.ReferenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("v1/referencias")
@RequiredArgsConstructor
@Tag(name = "Tabela de Preços de Referência", description = "Consulta e manutenção da tabela de preços de referência")
@SecurityRequirement(name = "bearerAuth")
public class ReferenciaController {
    private final ReferenciaService referenciaService;
    private final ReferenciaPdfExportService referenciaPdfExportService;
    private final ReferenciaExcelExportService referenciaExcelExportService;
    private final ReferenciaMapper mapper;

    @GetMapping()
    @Operation(
            summary = "Listar preços de referência",
            description = "Lista os itens da tabela de preços de referência, de forma paginada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    public ResponseEntity<Page<ReferenciaGetResponseDTO>> listAll(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {
        var response = referenciaService.listAll(pageable).map(mapper::toReferenciaGetResponseDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping()
    @Operation(
            summary = "Criar item de referência",
            description = "Cadastra um novo item na tabela de preços de referência.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    public ResponseEntity<ReferenciaGetResponseDTO> save(@RequestBody @Valid ReferenciaPostRequestDTO referenciaPostRequestDTO) {
        var referencia = mapper.toReferencia(referenciaPostRequestDTO);
        var referenciaSave = referenciaService.save(referencia);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toReferenciaGetResponseDTO(referenciaSave));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar item de referência",
            description = "Atualiza os dados de um item existente na tabela de preços de referência.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    public ResponseEntity<ReferenciaGetResponseDTO> updateById(
            @Parameter(description = "Identificador do item de referência", required = true)
            @PathVariable Integer id,
            @RequestBody @Valid ReferenciaPutRequestDTO referenciaPutRequestDTO) {
        var referencia = mapper.toReferencia(referenciaPutRequestDTO);
        var referenciaUpdated = referenciaService.updateById(id, referencia);

        return ResponseEntity.status(HttpStatus.OK).body(mapper.toReferenciaGetResponseDTO(referenciaUpdated));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Excluir item de referência",
            description = "Remove um item existente da tabela de preços de referência.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item excluído com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Item não encontrado")
    })
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Identificador do item de referência", required = true)
            @PathVariable Integer id) {
        referenciaService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/export/pdf")
    @Operation(
            summary = "Exportar tabela de referência em PDF",
            description = "Gera e retorna um PDF com todos os itens da tabela de preços de referência.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Arquivo gerado com sucesso",
                    content = @Content(mediaType = MediaType.APPLICATION_PDF_VALUE)),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    public ResponseEntity<byte[]> exportPdf() {
        byte[] pdf = referenciaPdfExportService.exportPdf();
        String nomeArquivo = "tabela-precos-referencia-" + LocalDate.now() + ".pdf";

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/export/excel")
    @Operation(
            summary = "Exportar tabela de referência em Excel",
            description = "Gera e retorna uma planilha (.xlsx) com todos os itens da tabela de preços de referência.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Arquivo gerado com sucesso",
                    content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    public ResponseEntity<byte[]> exportExcel() {
        byte[] excel = referenciaExcelExportService.exportExcel();
        String nomeArquivo = "tabela-precos-referencia-" + LocalDate.now() + ".xlsx";

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}

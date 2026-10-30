package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.FinanceiroPostRequestDTO;
import br.com.bw.backend.dto.request.FinanceiroPutRequestDTO;
import br.com.bw.backend.dto.response.FinanceiroGetResponseDTO;
import br.com.bw.backend.dto.response.FinanceiroKpiGetResponseDTO;
import br.com.bw.backend.dto.response.FinanceiroImportPostResponseDTO;
import br.com.bw.backend.dto.response.FinanceiroReceitaDespesaMensalGetResponseDTO;
import br.com.bw.backend.mapper.FinanceiroMapper;
import br.com.bw.backend.service.FinanceiroExcelExportService;
import br.com.bw.backend.service.FinanceiroExcelImportService;
import br.com.bw.backend.service.FinanceiroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("v1/financeiros")
@Tag(name = "Financeiro", description = "Lançamentos financeiros (receitas e despesas), KPIs e exportação/importação de planilhas")
@SecurityRequirement(name = "bearerAuth")
public class FinanceiroController {
    private final FinanceiroService financeiroService;
    private final FinanceiroExcelExportService financeiroExcelExportService;
    private final FinanceiroExcelImportService financeiroExcelImportService;
    private final FinanceiroMapper mapper;

    public FinanceiroController(
            FinanceiroService financeiroService,
            FinanceiroExcelExportService financeiroExcelExportService,
            FinanceiroExcelImportService financeiroExcelImportService,
            FinanceiroMapper mapper
    ) {
        this.financeiroService = financeiroService;
        this.financeiroExcelExportService = financeiroExcelExportService;
        this.financeiroExcelImportService = financeiroExcelImportService;
        this.mapper = mapper;
    }

    @GetMapping()
    @Operation(
            summary = "Listar lançamentos financeiros",
            description = "Lista os lançamentos financeiros de forma paginada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    public ResponseEntity<Page<FinanceiroGetResponseDTO>> findAll(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        var financeiroFindAll = financeiroService.findAll(pageable);
        var response = mapper.toFinanceiroGetResponseDTO(financeiroFindAll);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/kpis")
    @Operation(
            summary = "Consultar indicadores financeiros",
            description = "Retorna os principais indicadores (KPIs) financeiros, como total de receitas, despesas e saldo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "KPIs retornados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    public ResponseEntity<FinanceiroKpiGetResponseDTO> getKpis() {
        return ResponseEntity.status(HttpStatus.OK).body(financeiroService.getKpis());
    }

    @GetMapping("/receitas-despesas-mensal")
    @Operation(
            summary = "Comparativo mensal de receitas e despesas",
            description = "Retorna o total de receitas e despesas por mês, para os últimos N meses informados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Comparativo retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    public ResponseEntity<List<FinanceiroReceitaDespesaMensalGetResponseDTO>> getMonthlyComparison(
            @Parameter(description = "Quantidade de meses retroativos a considerar")
            @RequestParam(defaultValue = "6") Integer meses
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(financeiroService.getMonthlyComparison(meses));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Buscar lançamento financeiro por id",
            description = "Retorna os dados de um lançamento financeiro específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lançamento encontrado"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Lançamento não encontrado")
    })
    public ResponseEntity<FinanceiroGetResponseDTO> findById(
            @Parameter(description = "Identificador do lançamento financeiro", required = true)
            @PathVariable Integer id) {
        var financeiro = financeiroService.findById(id);
        var response = mapper.toFinanceiroGetResponseDTO(financeiro);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping()
    @Operation(
            summary = "Criar lançamento financeiro",
            description = "Cadastra uma nova receita ou despesa vinculada a um projeto.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lançamento criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    public ResponseEntity<FinanceiroGetResponseDTO> save(@RequestBody @Valid FinanceiroPostRequestDTO financeiroPostRequestDTO) {
        var financeiroSaved = financeiroService.save(mapper.toFinanceiro(financeiroPostRequestDTO));
        var response = mapper.toFinanceiroGetResponseDTO(financeiroSaved);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Atualizar lançamento financeiro",
            description = "Atualiza os dados de um lançamento financeiro existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lançamento atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Lançamento não encontrado")
    })
    public ResponseEntity<FinanceiroGetResponseDTO> update(
            @Parameter(description = "Identificador do lançamento financeiro", required = true)
            @PathVariable Integer id,
            @RequestBody @Valid FinanceiroPutRequestDTO financeiroPutRequestDTO
    ) {
        var financeiroUpdated = financeiroService.update(id, mapper.toFinanceiro(financeiroPutRequestDTO));
        var response = mapper.toFinanceiroGetResponseDTO(financeiroUpdated);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/export/excel")
    @Operation(
            summary = "Exportar lançamentos financeiros em Excel",
            description = "Gera e retorna uma planilha (.xlsx) com todos os lançamentos financeiros cadastrados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Arquivo gerado com sucesso",
                    content = @Content(
                            mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    public ResponseEntity<byte[]> exportExcel() {
        byte[] excel = financeiroExcelExportService.exportExcel();
        String nomeArquivo = "financeiro-" + LocalDate.now() + ".xlsx";

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @PostMapping("/import/excel")
    @Operation(
            summary = "Importar lançamentos financeiros via Excel",
            description = "Recebe uma planilha (.xlsx) e importa os lançamentos financeiros nela contidos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Importação concluída (ver detalhes no corpo da resposta)"),
            @ApiResponse(responseCode = "400", description = "Arquivo inválido ou com formato incorreto"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    public ResponseEntity<FinanceiroImportPostResponseDTO> importExcel(
            @Parameter(description = "Arquivo .xlsx com os lançamentos a importar", required = true)
            @RequestParam("arquivo") MultipartFile arquivo) {
        return ResponseEntity.status(HttpStatus.OK).body(financeiroExcelImportService.importExcel(arquivo));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Excluir lançamento financeiro",
            description = "Remove um lançamento financeiro existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Lançamento excluído com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
            @ApiResponse(responseCode = "404", description = "Lançamento não encontrado")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Identificador do lançamento financeiro", required = true)
            @PathVariable Integer id) {
        financeiroService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

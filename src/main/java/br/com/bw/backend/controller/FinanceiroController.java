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
    public ResponseEntity<Page<FinanceiroGetResponseDTO>> findAll(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        var financeiroFindAll = financeiroService.findAll(pageable);
        var response = mapper.toFinanceiroGetResponseDTO(financeiroFindAll);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/kpis")
    public ResponseEntity<FinanceiroKpiGetResponseDTO> getKpis() {
        return ResponseEntity.status(HttpStatus.OK).body(financeiroService.getKpis());
    }

    @GetMapping("/receitas-despesas-mensal")
    public ResponseEntity<List<FinanceiroReceitaDespesaMensalGetResponseDTO>> getMonthlyComparison(
            @RequestParam(defaultValue = "6") Integer meses
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(financeiroService.getMonthlyComparison(meses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinanceiroGetResponseDTO> findById(@PathVariable Integer id) {
        var financeiro = financeiroService.findById(id);
        var response = mapper.toFinanceiroGetResponseDTO(financeiro);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping()
    public ResponseEntity<FinanceiroGetResponseDTO> save(@RequestBody @Valid FinanceiroPostRequestDTO financeiroPostRequestDTO) {
        var financeiroSaved = financeiroService.save(mapper.toFinanceiro(financeiroPostRequestDTO));
        var response = mapper.toFinanceiroGetResponseDTO(financeiroSaved);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinanceiroGetResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody @Valid FinanceiroPutRequestDTO financeiroPutRequestDTO
    ) {
        var financeiroUpdated = financeiroService.update(id, mapper.toFinanceiro(financeiroPutRequestDTO));
        var response = mapper.toFinanceiroGetResponseDTO(financeiroUpdated);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel() {
        byte[] excel = financeiroExcelExportService.exportExcel();
        String nomeArquivo = "financeiro-" + LocalDate.now() + ".xlsx";

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @PostMapping("/import/excel")
    public ResponseEntity<FinanceiroImportPostResponseDTO> importExcel(@RequestParam("arquivo") MultipartFile arquivo) {
        return ResponseEntity.status(HttpStatus.OK).body(financeiroExcelImportService.importExcel(arquivo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        financeiroService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

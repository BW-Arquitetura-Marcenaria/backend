package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.FinanceiroPostRequestDTO;
import br.com.bw.backend.dto.request.FinanceiroPutRequestDTO;
import br.com.bw.backend.dto.response.FinanceiroGetResponseDTO;
import br.com.bw.backend.dto.response.FinanceiroKpiResponseDTO;
import br.com.bw.backend.dto.response.FinanceiroImportResultDTO;
import br.com.bw.backend.dto.response.FinanceiroReceitaDespesaMensalDTO;
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

        return ResponseEntity.ok(response);
    }

    @GetMapping("/kpis")
    public ResponseEntity<FinanceiroKpiResponseDTO> getKpis() {
        return ResponseEntity.ok(financeiroService.getKpis());
    }

    @GetMapping("/receitas-despesas-mensal")
    public ResponseEntity<List<FinanceiroReceitaDespesaMensalDTO>> getComparativoMensal(
            @RequestParam(defaultValue = "6") Integer meses
    ) {
        return ResponseEntity.ok(financeiroService.getComparativoMensal(meses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FinanceiroGetResponseDTO> findById(@PathVariable Integer id) {
        var financeiro = financeiroService.findById(id);
        var response = mapper.toFinanceiroGetResponseDTO(financeiro);

        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<FinanceiroGetResponseDTO> save(@RequestBody @Valid FinanceiroPostRequestDTO financeiroPostRequestDTO) {
        var financeiroSaved = financeiroService.save(mapper.toFinanceiro(financeiroPostRequestDTO));
        var response = mapper.toFinanceiroGetResponseDTO(financeiroSaved);

        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FinanceiroGetResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody @Valid FinanceiroPutRequestDTO financeiroPutRequestDTO
    ) {
        var financeiroUpdated = financeiroService.update(id, mapper.toFinanceiro(financeiroPutRequestDTO));
        var response = mapper.toFinanceiroGetResponseDTO(financeiroUpdated);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportarExcel() {
        byte[] excel = financeiroExcelExportService.exportarExcel();
        String nomeArquivo = "financeiro-" + LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }

    @PostMapping("/import/excel")
    public ResponseEntity<FinanceiroImportResultDTO> importarExcel(@RequestParam("arquivo") MultipartFile arquivo) {
        return ResponseEntity.ok(financeiroExcelImportService.importarExcel(arquivo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        financeiroService.delete(id);

        return ResponseEntity.noContent().build();
    }
}

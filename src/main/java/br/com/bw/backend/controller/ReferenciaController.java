package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.ReferenciaPostRequestDTO;
import br.com.bw.backend.dto.request.ReferenciaPutRequestDTO;
import br.com.bw.backend.dto.response.ReferenciaGetResponseDTO;
import br.com.bw.backend.mapper.ReferenciaMapper;
import br.com.bw.backend.service.ReferenciaExcelExportService;
import br.com.bw.backend.service.ReferenciaPdfExportService;
import br.com.bw.backend.service.ReferenciaService;
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
public class ReferenciaController {
    private final ReferenciaService referenciaService;
    private final ReferenciaPdfExportService referenciaPdfExportService;
    private final ReferenciaExcelExportService referenciaExcelExportService;
    private final ReferenciaMapper mapper;

    @GetMapping()
    public ResponseEntity<Page<ReferenciaGetResponseDTO>> listAll(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {
        var response = referenciaService.listAll(pageable).map(mapper::toReferenciaGetResponseDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping()
    public ResponseEntity<ReferenciaGetResponseDTO> save(@RequestBody @Valid ReferenciaPostRequestDTO referenciaPostRequestDTO) {
        var referencia = mapper.toReferencia(referenciaPostRequestDTO);
        var referenciaSave = referenciaService.save(referencia);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toReferenciaGetResponseDTO(referenciaSave));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReferenciaGetResponseDTO> updateById(@PathVariable Integer id, @RequestBody @Valid ReferenciaPutRequestDTO referenciaPutRequestDTO) {
        var referencia = mapper.toReferencia(referenciaPutRequestDTO);
        var referenciaUpdated = referenciaService.updateById(id, referencia);

        return ResponseEntity.status(HttpStatus.OK).body(mapper.toReferenciaGetResponseDTO(referenciaUpdated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        referenciaService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf() {
        byte[] pdf = referenciaPdfExportService.exportPdf();
        String nomeArquivo = "tabela-precos-referencia-" + LocalDate.now() + ".pdf";

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel() {
        byte[] excel = referenciaExcelExportService.exportExcel();
        String nomeArquivo = "tabela-precos-referencia-" + LocalDate.now() + ".xlsx";

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nomeArquivo + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excel);
    }
}

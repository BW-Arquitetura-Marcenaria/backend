package br.com.bw.backend.controller;

import br.com.bw.backend.dto.response.ReferenciaGetResponseDTO;
import br.com.bw.backend.mapper.ReferenciaMapper;
import br.com.bw.backend.service.ReferenciaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/referencias")
public class ReferenciaController {
    private final ReferenciaService referenciaService;
    private final ReferenciaMapper mapper;

    public ReferenciaController(ReferenciaService referenciaService, ReferenciaMapper mapper) {
        this.referenciaService = referenciaService;
        this.mapper = mapper;
    }

    @GetMapping()
    public ResponseEntity<Page<ReferenciaGetResponseDTO>> listAll(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {
        var response = referenciaService.listAll(pageable).map(mapper::toReferenciaGetResponseDTO);
        return ResponseEntity.ok(response);
    }
}

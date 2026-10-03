package br.com.bw.backend.controller;

import br.com.bw.backend.dto.response.RastreabilidadeGetResponseDTO;
import br.com.bw.backend.mapper.RastreabilidadeMapper;
import br.com.bw.backend.service.RastreabilidadeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/rastreabilidades")
public class RastreabilidadeController {
    private final RastreabilidadeService rastreabilidadeService;
    private final RastreabilidadeMapper mapper;

    public RastreabilidadeController(RastreabilidadeService rastreabilidadeService, RastreabilidadeMapper mapper) {
        this.rastreabilidadeService = rastreabilidadeService;
        this.mapper = mapper;
    }

    @GetMapping()
    public ResponseEntity<Page<RastreabilidadeGetResponseDTO>> findAll(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        var rastreabilidadeFindAll = rastreabilidadeService.findAll(pageable);
        var response = mapper.toRastreabilidadeGetResponseDTO(rastreabilidadeFindAll);

        return ResponseEntity.ok(response);
    }
}

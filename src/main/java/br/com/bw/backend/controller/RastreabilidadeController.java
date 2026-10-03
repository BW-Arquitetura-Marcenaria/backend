package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.RastreabilidadePostRequestDTO;
import br.com.bw.backend.dto.response.RastreabilidadeGetResponseDTO;
import br.com.bw.backend.mapper.RastreabilidadeMapper;
import br.com.bw.backend.service.RastreabilidadeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping()
    public ResponseEntity<RastreabilidadeGetResponseDTO> save(@RequestBody @Valid RastreabilidadePostRequestDTO rastreabilidadePostRequestDTO) {
        var rastreabilidadeSaved = rastreabilidadeService.save(mapper.toRastreabilidade(rastreabilidadePostRequestDTO));
        var response = mapper.toRastreabilidadeGetResponseDTO(rastreabilidadeSaved);

        return ResponseEntity.status(201).body(response);
    }
}

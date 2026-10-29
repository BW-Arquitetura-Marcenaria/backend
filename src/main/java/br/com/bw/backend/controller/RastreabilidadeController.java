package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.RastreabilidadePostRequestDTO;
import br.com.bw.backend.dto.request.RastreabilidadePutRequestDTO;
import br.com.bw.backend.dto.response.RastreabilidadeGetResponseDTO;
import br.com.bw.backend.mapper.RastreabilidadeMapper;
import br.com.bw.backend.service.RastreabilidadeService;
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

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping()
    public ResponseEntity<RastreabilidadeGetResponseDTO> save(@RequestBody @Valid RastreabilidadePostRequestDTO rastreabilidadePostRequestDTO) {
        var rastreabilidadeSaved = rastreabilidadeService.save(mapper.toRastreabilidade(rastreabilidadePostRequestDTO));
        var response = mapper.toRastreabilidadeGetResponseDTO(rastreabilidadeSaved);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RastreabilidadeGetResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody @Valid RastreabilidadePutRequestDTO rastreabilidadePutRequestDTO
    ) {
        var rastreabilidadeUpdated = rastreabilidadeService.update(id, mapper.toRastreabilidade(rastreabilidadePutRequestDTO));
        var response = mapper.toRastreabilidadeGetResponseDTO(rastreabilidadeUpdated);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        rastreabilidadeService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("projeto/{projetoId}")
    public ResponseEntity<Page<RastreabilidadeGetResponseDTO>> listAllByProjeto(
            @PathVariable Integer projetoId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        var response = rastreabilidadeService.listAllByProjeto(projetoId, pageable)
                .map(mapper::toRastreabilidadeGetResponseDTO);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

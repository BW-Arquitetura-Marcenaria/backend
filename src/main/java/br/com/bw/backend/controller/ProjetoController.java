package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.ProjetoPostRequestDTO;
import br.com.bw.backend.dto.request.ProjetoPutRequestDTO;
import br.com.bw.backend.dto.response.ProjetoGetResponseDTO;
import br.com.bw.backend.mapper.ProjetoMapper;
import br.com.bw.backend.service.ProjetoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/projetos")
public class ProjetoController {
    private final ProjetoService projetoService;
    private final ProjetoMapper mapper;

    public ProjetoController(ProjetoService projetoService, ProjetoMapper mapper) {
        this.projetoService = projetoService;
        this.mapper = mapper;
    }

    @GetMapping()
    public ResponseEntity<Page<ProjetoGetResponseDTO>> findAll(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        var projetoFindAll = projetoService.findAll(pageable);
        var response = mapper.toProjetoGetResponseDTO(projetoFindAll);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjetoGetResponseDTO> findById(@PathVariable Integer id) {
        var projeto = projetoService.findById(id);
        var response = mapper.toProjetoGetResponseDTO(projeto);

        return ResponseEntity.ok(response);
    }

    @PostMapping()
    public ResponseEntity<ProjetoGetResponseDTO> save(@RequestBody @Valid ProjetoPostRequestDTO projetoPostRequestDTO) {
        var projetoSaved = projetoService.save(mapper.toProjeto(projetoPostRequestDTO));
        var response = mapper.toProjetoGetResponseDTO(projetoSaved);

        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjetoGetResponseDTO> update(
            @PathVariable Integer id,
            @RequestBody @Valid ProjetoPutRequestDTO projetoPutRequestDTO
    ) {
        var projetoUpdated = projetoService.update(id, mapper.toProjeto(projetoPutRequestDTO));
        var response = mapper.toProjetoGetResponseDTO(projetoUpdated);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        projetoService.delete(id);

        return ResponseEntity.noContent().build();
    }
}

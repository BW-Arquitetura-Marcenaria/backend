package br.com.bw.backend.controller;

import br.com.bw.backend.dto.request.ReferenciaPostRequestDTO;
import br.com.bw.backend.dto.request.ReferenciaPutRequestDTO;
import br.com.bw.backend.dto.response.ReferenciaGetResponseDTO;
import br.com.bw.backend.mapper.ReferenciaMapper;
import br.com.bw.backend.service.ReferenciaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping()
    public ResponseEntity<ReferenciaGetResponseDTO> save(@RequestBody ReferenciaPostRequestDTO referenciaPostRequestDTO){
        var referencia = mapper.toReferencia(referenciaPostRequestDTO);
        var referenciaSave = referenciaService.save(referencia);

        return ResponseEntity.status(201).body(mapper.toReferenciaGetResponseDTO(referenciaSave));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReferenciaGetResponseDTO> updateById(@PathVariable Integer id, @RequestBody ReferenciaPutRequestDTO referenciaPutRequestDTO) {
        var referencia = mapper.toReferencia(referenciaPutRequestDTO);
        var referenciaUpdated = referenciaService.updateById(id, referencia);

        return ResponseEntity.ok(mapper.toReferenciaGetResponseDTO(referenciaUpdated));
    }
}

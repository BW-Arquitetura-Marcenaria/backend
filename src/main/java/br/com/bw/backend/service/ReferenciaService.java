package br.com.bw.backend.service;

import br.com.bw.backend.entity.Referencia;
import br.com.bw.backend.exception.NotFoundException;
import br.com.bw.backend.repository.ReferenciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReferenciaService {
    private final ReferenciaRepository referenciaRepository;

    public Page<Referencia> listAll(Pageable pageable) {
        log.info("Listando referências de preços - página: {}, tamanho: {}", pageable.getPageNumber(), pageable.getPageSize());
        return referenciaRepository.findAll(pageable);
    }

    public Referencia save(Referencia referencia) {
        referencia.setDataAtualizacao(LocalDateTime.now());
        var referenciaSalva = referenciaRepository.save(referencia);
        log.info("Referência criada com sucesso - id: {}, categoria: {}", referenciaSalva.getId(), referenciaSalva.getCategoria());
        return referenciaSalva;
    }

    @Transactional
    public Referencia updateById(Integer id, Referencia referencia) {
        log.info("Atualizando referência - id: {}", id);

        var atual = referenciaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Referência não encontrada para atualização - id: {}", id);
                    return new NotFoundException("Referência não encontrada");
                });

        atual.setCategoria(referencia.getCategoria());
        atual.setPrecoReferencia(referencia.getPrecoReferencia());
        atual.setDescricao(referencia.getDescricao());
        atual.setMaterialBase(referencia.getMaterialBase());
        atual.setUnidade(referencia.getUnidade());
        atual.setDataAtualizacao(LocalDateTime.now());

        var referenciaAtualizada = referenciaRepository.save(atual);
        log.info("Referência atualizada com sucesso - id: {}", referenciaAtualizada.getId());
        return referenciaAtualizada;
    }

    @Transactional
    public void deleteById(Integer id) {
        log.info("Removendo referência - id: {}", id);

        referenciaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Referência não encontrada para remoção - id: {}", id);
                    return new NotFoundException("Referência não encontrada");
                });

        referenciaRepository.deleteById(id);
        log.info("Referência removida com sucesso - id: {}", id);
    }
}

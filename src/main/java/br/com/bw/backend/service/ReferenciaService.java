package br.com.bw.backend.service;

import br.com.bw.backend.entity.Referencia;
import br.com.bw.backend.exception.NotFoundException;
import br.com.bw.backend.repository.ReferenciaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReferenciaService {
    private final ReferenciaRepository referenciaRepository;

    public ReferenciaService(ReferenciaRepository referenciaRepository) {
        this.referenciaRepository = referenciaRepository;
    }

    public Page<Referencia> listAll(Pageable pageable) {
        return referenciaRepository.findAll(pageable);
    }

    public Referencia save(Referencia referencia) {
        referencia.setDataAtualizacao(LocalDateTime.now());
        return referenciaRepository.save(referencia);
    }

    @Transactional
    public Referencia updateById(Integer id, Referencia referencia) {
        var atual = referenciaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Referência não encontrada"));

        atual.setCategoria(referencia.getCategoria());
        atual.setPrecoReferencia(referencia.getPrecoReferencia());
        atual.setDescricao(referencia.getDescricao());
        atual.setMaterialBase(referencia.getMaterialBase());
        atual.setUnidade(referencia.getUnidade());
        atual.setDataAtualizacao(LocalDateTime.now());

        return referenciaRepository.save(atual);
    }

    @Transactional
    public void deleteById(Integer id) {
        referenciaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Referência não encontrada"));

        referenciaRepository.deleteById(id);
    }
}

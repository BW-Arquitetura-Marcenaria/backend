package br.com.bw.backend.service;

import br.com.bw.backend.entity.Financeiro;
import br.com.bw.backend.exception.NotFoundException;
import br.com.bw.backend.repository.FinanceiroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FinanceiroService {
    private final FinanceiroRepository financeiroRepository;

    public Page<Financeiro> findAll(Pageable pageable) {
        log.info("Listando registros financeiros - página: {}, tamanho: {}", pageable.getPageNumber(), pageable.getPageSize());
        return financeiroRepository.findAll(pageable);
    }

    public Financeiro findById(Integer id) {
        return financeiroRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Registro financeiro não encontrado"));
    }

    public Financeiro save(Financeiro financeiro) {
        var financeiroSalvo = financeiroRepository.save(financeiro);
        log.info("Registro financeiro criado com sucesso - id: {}", financeiroSalvo.getId());
        return financeiroSalvo;
    }

    public Financeiro update(Integer id, Financeiro financeiro) {
        log.info("Atualizando registro financeiro - id: {}", id);

        var financeiroExistente = findById(id);
        financeiro.setId(financeiroExistente.getId());

        return financeiroRepository.save(financeiro);
    }

    public void delete(Integer id) {
        log.info("Removendo registro financeiro - id: {}", id);

        var financeiro = findById(id);
        financeiroRepository.delete(financeiro);
        log.info("Registro financeiro removido com sucesso - id: {}", id);
    }
}

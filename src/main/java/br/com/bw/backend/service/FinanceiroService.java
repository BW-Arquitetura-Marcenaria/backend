package br.com.bw.backend.service;

import br.com.bw.backend.dto.response.FinanceiroKpiGetResponseDTO;
import br.com.bw.backend.dto.response.FinanceiroReceitaDespesaMensalGetResponseDTO;
import br.com.bw.backend.entity.Financeiro;
import br.com.bw.backend.exception.NotFoundException;
import br.com.bw.backend.repository.FinanceiroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
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

    public FinanceiroKpiGetResponseDTO getKpis() {
        var mesAtual = YearMonth.now();
        var mesAnterior = mesAtual.minusMonths(1);

        var receitasMes = financeiroRepository.sumValorRecebidoFinalByDataPedidoBetween(
                mesAtual.atDay(1), mesAtual.atEndOfMonth());
        var receitasMesAnterior = financeiroRepository.sumValorRecebidoFinalByDataPedidoBetween(
                mesAnterior.atDay(1), mesAnterior.atEndOfMonth());

        var despesasMes = financeiroRepository.sumFreteByDataPedidoBetween(
                mesAtual.atDay(1), mesAtual.atEndOfMonth());
        var despesasMesAnterior = financeiroRepository.sumFreteByDataPedidoBetween(
                mesAnterior.atDay(1), mesAnterior.atEndOfMonth());

        var resultadoLiquido = receitasMes.subtract(despesasMes);
        var margemPercentual = calculatePercentage(resultadoLiquido, receitasMes);

        return new FinanceiroKpiGetResponseDTO(
                receitasMes.setScale(2, RoundingMode.HALF_UP),
                calculatePercentageVariation(receitasMes, receitasMesAnterior),
                despesasMes.setScale(2, RoundingMode.HALF_UP),
                calculatePercentageVariation(despesasMes, despesasMesAnterior),
                resultadoLiquido.setScale(2, RoundingMode.HALF_UP),
                margemPercentual
        );
    }

    private BigDecimal calculatePercentageVariation(BigDecimal valorAtual, BigDecimal valorAnterior) {
        if (valorAnterior.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return valorAtual.subtract(valorAnterior)
                .divide(valorAnterior, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePercentage(BigDecimal numerador, BigDecimal denominador) {
        if (denominador.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return numerador.divide(denominador, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);
    }

    public List<FinanceiroReceitaDespesaMensalGetResponseDTO> getMonthlyComparison(int meses) {
        if (meses < 1) {
            meses = 1;
        }

        var mesAtual = YearMonth.now();
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        var comparativo = new ArrayList<FinanceiroReceitaDespesaMensalGetResponseDTO>();

        for (int i = meses - 1; i >= 0; i--) {
            var mesReferencia = mesAtual.minusMonths(i);

            var receitas = financeiroRepository.sumValorRecebidoFinalByDataPedidoBetween(
                    mesReferencia.atDay(1), mesReferencia.atEndOfMonth());
            var despesas = financeiroRepository.sumFreteByDataPedidoBetween(
                    mesReferencia.atDay(1), mesReferencia.atEndOfMonth());

            comparativo.add(new FinanceiroReceitaDespesaMensalGetResponseDTO(
                    mesReferencia.format(formatter),
                    receitas.setScale(2, RoundingMode.HALF_UP),
                    despesas.setScale(2, RoundingMode.HALF_UP)
            ));
        }

        return comparativo;
    }
}

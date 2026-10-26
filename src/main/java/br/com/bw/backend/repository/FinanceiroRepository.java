package br.com.bw.backend.repository;

import br.com.bw.backend.entity.Financeiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface FinanceiroRepository extends JpaRepository<Financeiro, Integer> {

    @Query("""
            SELECT COALESCE(SUM(f.valorRecebidoFinal), 0)
              FROM Financeiro f
             WHERE f.dataPedido BETWEEN :inicio AND :fim
            """)
    BigDecimal sumValorRecebidoFinalByDataPedidoBetween(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("""
            SELECT COALESCE(SUM(f.frete), 0)
              FROM Financeiro f
             WHERE f.dataPedido BETWEEN :inicio AND :fim
            """)
    BigDecimal sumFreteByDataPedidoBetween(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}

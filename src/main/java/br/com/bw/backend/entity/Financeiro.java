package br.com.bw.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "financeiro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Financeiro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    private String descricao;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private BigDecimal frete;

    @Column(name = "valor_total", nullable = false)
    private BigDecimal valorTotal;

    @Column(name = "valor_final_desconto")
    private BigDecimal valorFinalDesconto;

    @Column(name = "valor_entrada")
    private BigDecimal valorEntrada;

    @Column(name = "receber_entrega")
    private BigDecimal receberEntrega;

    @Column(name = "valor_recebido_final")
    private BigDecimal valorRecebidoFinal;

    @Column(name = "valor_total_liquido")
    private BigDecimal valorTotalLiquido;

    @Column(name = "data_pedido")
    private LocalDate dataPedido;

    @Column(name = "data_entrega")
    private LocalDate dataEntrega;

    @Column(name = "contato_cliente", length = 100)
    private String contatoCliente;

    @Column(name = "regiao_venda", length = 100)
    private String regiaoVenda;
}

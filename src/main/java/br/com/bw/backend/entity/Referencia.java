package br.com.bw.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "tabela_precos_referencia")
public class Referencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    @Column(name = "item_servico", nullable = false, length = 150)
    private String itemService;
    @Column(name = "preco_referencia", nullable = false)
    private BigDecimal precoReferencia;
    private String descricao;
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
}

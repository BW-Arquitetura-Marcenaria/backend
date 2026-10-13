package br.com.bw.backend.entity;

import br.com.bw.backend.entity.enums.CategoriaReferencia;
import br.com.bw.backend.entity.enums.UnidadeReferencia;
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
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaReferencia categoria;
    @Column(name = "preco_referencia", nullable = false)
    private BigDecimal precoReferencia;
    @Column(nullable = false)
    private String descricao;
    @Column(name = "material_base", nullable = false, length = 150)
    private String materialBase;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadeReferencia unidade;
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
}

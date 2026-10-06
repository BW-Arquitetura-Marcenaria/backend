package br.com.bw.backend.entity;

import br.com.bw.backend.entity.enums.StatusTransacao;
import br.com.bw.backend.entity.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "fluxo_financeiro")
public class Financeiro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;


    @Column(name = "tipo_transacao", nullable = false, length = 10)
    private TipoTransacao tipoTransacao;
    @Column(nullable = false)
    private BigDecimal valor;
    @Column(name = "data_transacao", nullable = false)
    private LocalDateTime dataTransacao;
    @Column(length = 100)
    private String categoria;
    private String descricao;
    @Column(length = 20)
    private StatusTransacao status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;
}

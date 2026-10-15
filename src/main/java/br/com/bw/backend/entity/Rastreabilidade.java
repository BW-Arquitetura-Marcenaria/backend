package br.com.bw.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "rastreabilidade_madeira")
public class Rastreabilidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    @Column(length = 100, nullable = false)
    private String lote;
    @Column(name = "tipo_madeira", length = 100, nullable = false)
    private String tipoMadeira;
    @Column(length = 150, nullable = false)
    private String fornecedor;
    @Column(length = 150, nullable = false)
    private String origem;
    @Column(nullable = false)
    private String certificacoes;
    @Column(length = 100, nullable = false)
    private String volume;
    @Column(name = "data_registro")
    private LocalDateTime dataRegistro;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;
}

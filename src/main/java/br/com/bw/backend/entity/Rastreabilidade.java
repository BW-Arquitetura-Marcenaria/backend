package br.com.bw.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
    @Column(length = 150)
    private String origem;
    private String certificacoes;
    @Column(name = "data_registro")
    private LocalDate dataRegistro;
}

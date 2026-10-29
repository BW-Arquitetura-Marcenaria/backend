package br.com.bw.backend.entity;

import br.com.bw.backend.entity.enums.StatusProjeto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "projetos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Projeto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Usuario cliente;
    @Column(name = "titulo_projeto", length = 150, nullable = false)
    private String tituloProjeto;
    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal;
    private StatusProjeto status;
}

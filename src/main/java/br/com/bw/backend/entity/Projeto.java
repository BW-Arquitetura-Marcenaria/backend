package br.com.bw.backend.entity;

import br.com.bw.backend.entity.enums.StatusProjeto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "projetos")
public class Projeto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    @Column(name = "titulo_projeto", length = 150, nullable = false)
    private String tituloProjeto;
    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal;
    @Enumerated(EnumType.STRING)
    private StatusProjeto status;
}

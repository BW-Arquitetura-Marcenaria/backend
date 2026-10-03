package br.com.bw.backend.entity;

import br.com.bw.backend.entity.enums.StatusProjeto;
import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "valor_total")
    private Double valorTotal;
    private StatusProjeto status;
}

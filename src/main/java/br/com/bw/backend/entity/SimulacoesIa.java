package br.com.bw.backend.entity;

import br.com.bw.backend.entity.enums.Ambiente;
import br.com.bw.backend.entity.enums.EstiloDesejado;
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
@Table(name = "simulacoes_ia")
public class SimulacoesIa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    @Column(name = "descricao_prompt", nullable = false)
    private String descricaoPrompt;
    private Ambiente ambiente;
    @Column(name = "estilo_desejado", nullable = false)
    private EstiloDesejado estiloDesejado;
    private String acabamento;
    @Column(name = "imagem_gerada_url")
    private String imagemGeradaUrl;
    @Column(name = "data_simulacao", nullable = false)
    private LocalDateTime dataSimulacao;
}

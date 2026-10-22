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
@Table(name = "estado_oauth_google")
public class EstadoOAuthGoogle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(name = "hash_estado", nullable = false, length = 64)
    private String hashEstado;

    @Column(name = "hash_vinculo_navegador", nullable = false, length = 64)
    private String hashVinculoNavegador;

    @Column(name = "data_expiracao", nullable = false)
    private LocalDateTime dataExpiracao;

    @Column(name = "data_consumo")
    private LocalDateTime dataConsumo;

    @Column(name = "data_criacao", insertable = false, updatable = false)
    private LocalDateTime dataCriacao;
}
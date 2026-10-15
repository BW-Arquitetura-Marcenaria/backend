package br.com.bw.backend.entity;

import br.com.bw.backend.entity.enums.TipoEvento;
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
@Table(name = "calendario_eventos")
public class Calendario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    @Column(name = "tipo_evento", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TipoEvento tipoEvento;
    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;
    @Column(name = "data_fim", nullable = false)
    private LocalDateTime dataFim;
    private String observacoes;
}

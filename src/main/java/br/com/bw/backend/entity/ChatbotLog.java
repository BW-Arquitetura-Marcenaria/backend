package br.com.bw.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chatbot_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChatbotLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Usuario cliente;

    @Column(name = "instagram_id", length = 40)
    private String instagramId;

    @Column(name = "mensagem_usuario", nullable = false, columnDefinition = "TEXT")
    private String mensagemUsuario;

    @Column(name = "resposta_ia", nullable = false, columnDefinition = "TEXT")
    private String respostaIa;

    @Column(name = "data_interacao", insertable = false, updatable = false)
    private LocalDateTime dataInteracao;
}

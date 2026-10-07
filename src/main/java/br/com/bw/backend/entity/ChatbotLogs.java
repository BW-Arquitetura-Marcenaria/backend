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
@Table(name = "chatbot_logs")
public class ChatbotLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    @Column(name = "mensagem_usuario", nullable = false)
    private String mensagemUsuario;
    @Column(name = "resposta_ia", nullable = false)
    private String respostaIa;
    private LocalDateTime dataInteracao;
}

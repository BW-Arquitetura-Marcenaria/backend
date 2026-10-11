package br.com.bw.backend.entity;

import br.com.bw.backend.entity.enums.OrigemChat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chatbot_logs")
@Getter
@Setter
public class ChatbotLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Usuario cliente;

    @Column(name = "instagram_id", length = 40)
    private String instagramId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrigemChat origem = OrigemChat.INSTAGRAM;

    @Column(name = "mensagem_usuario", nullable = false, columnDefinition = "TEXT")
    private String mensagemUsuario;

    @Column(name = "resposta_ia", nullable = false, columnDefinition = "TEXT")
    private String respostaIa;

    @Column(name = "data_interacao", insertable = false, updatable = false)
    private LocalDateTime dataInteracao;
}
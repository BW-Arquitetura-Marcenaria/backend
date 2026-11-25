package br.com.bw.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "lead_instagram")
@Getter
@Setter
public class LeadInstagram {

    public static final String ORIGEM_INSTAGRAM = "Instagram";
    public static final String ORIGEM_PLATAFORMA = "Plataforma";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "instagram_id", length = 40)
    private String instagramId;

    @Column(nullable = false, length = 20)
    private String origem = ORIGEM_INSTAGRAM;

    @Column(name = "crm_lead_id", length = 30)
    private String crmLeadId;

    private String nome;

    @Column(length = 150)
    private String email;

    @Column(name = "sao_paulo")
    private Boolean saoPaulo = false;

    private String ambiente;
    private String movel;
    private String cor;
    private String material;
    private BigDecimal orcamento;

    @Column(name = "situacao_projeto")
    private String situacaoProjeto;

    @Column(name = "precisa_visita_tecnica")
    private Boolean precisaVisitaTecnica = false;

    @Column(name = "resumo_conversa", columnDefinition = "TEXT")
    private String resumoConversa;

    @Column(name = "score")
    private Integer score;

    @Column(name = "classificacao", length = 20)
    private String classificacao;

    @Column(name = "token_hash", length = 64)
    private String tokenHash;

    @Column(name = "token_expira_em")
    private LocalDateTime tokenExpiraEm;

    @Column(name = "token_usado_em")
    private LocalDateTime tokenUsadoEm;

    @Column(name = "link_enviado_em")
    private LocalDateTime linkEnviadoEm;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "criado_em", insertable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", insertable = false, updatable = false)
    private LocalDateTime atualizadoEm;
}
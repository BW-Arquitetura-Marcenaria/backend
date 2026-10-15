package br.com.bw.backend.entity;

import br.com.bw.backend.entity.enums.EtapaFunil;
import br.com.bw.backend.entity.enums.Perfil;
import br.com.bw.backend.entity.enums.TipoCliente;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(name = "tipo_cliente", nullable = false, length = 20)
    private TipoCliente tipoCliente;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String sobrenome;

    @Column(length = 20)
    private String cpf;

    @Column(length = 20)
    private String cnpj;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 20)
    private String telefone;

    @Builder.Default
    @Column(name = "etapa_funil", length = 20)
    private EtapaFunil etapaFunil = EtapaFunil.NOVO_LEAD;

    @CreationTimestamp
    @Column(name = "data_cadastro", updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "instagram_id", length = 40)
    private String instagramId;

    @Column(name = "senha_hash", length = 100)
    private String senhaHash;

    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Perfil perfil = Perfil.CLIENTE;
}
package br.com.bw.backend.entity;

import br.com.bw.backend.entity.enums.StatusSignatario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assinatura_signatarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AssinaturaSignatario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assinatura_id", nullable = false)
    private AssinaturaContrato assinatura;

    @Column(name = "signer_id", length = 60, nullable = false)
    private String signerId;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 20)
    private String cpf;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSignatario status = StatusSignatario.PENDENTE;
}

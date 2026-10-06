package br.com.bw.backend.entity;

import br.com.bw.backend.entity.enums.EtapaFunil;
import br.com.bw.backend.entity.enums.TipoCliente;
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
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    @Column(name = "tipo_cliente", nullable = false)
    private TipoCliente tipoCliente;
    private String nome;
    private String sobrenome;
    private String cpf;
    private String cnpj;
    @Column(name = "data_nascimento")
    private LocalDateTime dataNascimento;
    private String email;
    private String telefone;
    @Column(name = "etapa_funil")
    private EtapaFunil etapaFunil;
    private LocalDateTime dataCadastro;
}

package br.com.bw.backend.service;

import br.com.bw.backend.dto.request.ContaAdminEmpresaPutRequestDTO;
import br.com.bw.backend.dto.request.ContaAdminEnderecoPutRequestDTO;
import br.com.bw.backend.dto.request.ContaAdminSegurancaPutRequestDTO;
import br.com.bw.backend.dto.response.ContaAdminGetResponseDTO;
import br.com.bw.backend.entity.Empresa;
import br.com.bw.backend.entity.Endereco;
import br.com.bw.backend.entity.Usuario;
import br.com.bw.backend.entity.enums.Perfil;
import br.com.bw.backend.exception.BusinessValidationException;
import br.com.bw.backend.repository.EnderecoRepository;
import br.com.bw.backend.repository.UsuarioRepository;
import br.com.bw.backend.security.UsuarioLogado;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContaAdminService {

    private final UsuarioRepository usuarioRepository;
    private final EnderecoRepository enderecoRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public ContaAdminGetResponseDTO find() {
        Usuario admin = loggedInAdmin();
        return buildResponse(companyOf(admin));
    }

    @Transactional
    public ContaAdminGetResponseDTO updateCompany(ContaAdminEmpresaPutRequestDTO dto) {
        Usuario admin = loggedInAdmin();
        Empresa empresa = companyOf(admin);

        empresa.setRazaoSocial(dto.nome().trim());
        empresa.setNomeFantasia(isBlank(dto.nomeFantasia()) ? null : dto.nomeFantasia().trim());
        empresa.setTelefone(dto.telefone().trim());

        log.info("Dados da empresa atualizados - empresa: {}, admin: {}", empresa.getId(), admin.getId());
        return buildResponse(empresa);
    }

    @Transactional
    public ContaAdminGetResponseDTO updateAddress(ContaAdminEnderecoPutRequestDTO dto) {
        Usuario admin = loggedInAdmin();
        Empresa empresa = companyOf(admin);

        String cep = onlyDigits(dto.cep());
        if (cep.length() != 8) {
            throw new BusinessValidationException("CEP inválido");
        }
        String uf = dto.uf().trim().toUpperCase();
        if (!uf.matches("[A-Z]{2}")) {
            throw new BusinessValidationException("UF inválida");
        }

        Endereco endereco = enderecoRepository.findByEmpresaId(empresa.getId())
                .orElseGet(() -> Endereco.builder().empresa(empresa).build());
        endereco.setCep(cep);
        endereco.setLogradouro(dto.logradouro().trim());
        endereco.setNumero(dto.numero().trim());
        endereco.setComplemento(isBlank(dto.complemento()) ? null : dto.complemento().trim());
        endereco.setBairro(dto.bairro().trim());
        endereco.setCidade(dto.cidade().trim());
        endereco.setUf(uf);
        enderecoRepository.save(endereco);

        log.info("Endereço atualizado - empresa: {}", empresa.getId());
        return buildResponse(empresa);
    }

    @Transactional
    public void changePassword(ContaAdminSegurancaPutRequestDTO dto) {
        Usuario admin = loggedInAdmin();
        Empresa empresa = companyOf(admin);

        if (!onlyDigits(dto.cnpj()).equals(onlyDigits(empresa.getCnpj()))) {
            log.warn("Troca de senha negada - CNPJ não confere: admin {}", admin.getId());
            throw new BusinessValidationException("CNPJ não confere com o da empresa");
        }
        if (!passwordEncoder.matches(dto.senhaAtual(), admin.getSenhaHash())) {
            log.warn("Troca de senha negada - senha atual incorreta: admin {}", admin.getId());
            throw new BusinessValidationException("Senha atual incorreta");
        }
        if (dto.novaSenha().length() < 8 || dto.novaSenha().length() > 72) {
            throw new BusinessValidationException("A nova senha deve ter entre 8 e 72 caracteres");
        }
        if (!dto.novaSenha().equals(dto.confirmarNovaSenha())) {
            throw new BusinessValidationException("A confirmação não confere com a nova senha");
        }
        if (dto.novaSenha().equals(dto.senhaAtual())) {
            throw new BusinessValidationException("A nova senha deve ser diferente da atual");
        }

        admin.setSenhaHash(passwordEncoder.encode(dto.novaSenha()));
        log.info("Senha do admin alterada - id: {}", admin.getId());
    }

    private ContaAdminGetResponseDTO buildResponse(Empresa empresa) {
        var dadosEmpresa = new ContaAdminGetResponseDTO.EmpresaDados(
                empresa.getRazaoSocial(),
                empresa.getNomeFantasia(),
                empresa.getCnpj(),
                empresa.getTelefone());

        var dadosEndereco = enderecoRepository.findByEmpresaId(empresa.getId())
                .map(e -> new ContaAdminGetResponseDTO.EnderecoDados(
                        e.getCep(), e.getLogradouro(), e.getNumero(), e.getComplemento(),
                        e.getBairro(), e.getCidade(), e.getUf()))
                .orElse(null);

        return new ContaAdminGetResponseDTO(dadosEmpresa, dadosEndereco);
    }

    private Usuario loggedInAdmin() {
        Usuario usuario = usuarioRepository.findById(UsuarioLogado.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        if (usuario.getPerfil() != Perfil.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }
        return usuario;
    }

    private Empresa companyOf(Usuario admin) {
        if (admin.getEmpresa() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Nenhuma empresa vinculada a este administrador");
        }
        return admin.getEmpresa();
    }

    private String onlyDigits(String valor) {
        return valor == null ? "" : valor.replaceAll("\\D", "");
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.isBlank();
    }
}
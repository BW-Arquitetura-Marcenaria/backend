package br.com.bw.backend.service;

import br.com.bw.backend.dto.request.MinhaContaPutRequestDTO;
import br.com.bw.backend.dto.response.MinhaContaGetResponseDTO;
import br.com.bw.backend.entity.Empresa;
import br.com.bw.backend.entity.Usuario;
import br.com.bw.backend.entity.enums.Perfil;
import br.com.bw.backend.exception.BusinessValidationException;
import br.com.bw.backend.exception.ConflictException;
import br.com.bw.backend.mapper.MinhaContaMapper;
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
public class MinhaContaService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MinhaContaMapper mapper;

    @Transactional(readOnly = true)
    public MinhaContaGetResponseDTO find() {
        return mapper.toResponse(loggedInUser());
    }

    @Transactional
    public MinhaContaGetResponseDTO update(MinhaContaPutRequestDTO dto) {
        Usuario usuario = loggedInUser();
        String email = dto.email().trim().toLowerCase();

        if (!email.equals(usuario.getEmail()) && usuarioRepository.existsByEmail(email)) {
            log.warn("Atualização negada - e-mail já cadastrado: {}", email);
            throw new ConflictException("E-mail já cadastrado");
        }

        boolean admin = usuario.getPerfil() == Perfil.ADMIN;
        Empresa empresa = admin ? null : usuario.getEmpresa();
        if (empresa != null && isBlank(dto.razaoSocial())) {
            throw new BusinessValidationException("Razão social é obrigatória");
        }

        boolean trocaSenha = !isBlank(dto.senhaAtual()) || !isBlank(dto.novaSenha()) || !isBlank(dto.confirmarNovaSenha());
        if (trocaSenha) {
            if (admin) {
                throw new BusinessValidationException(
                        "A senha da conta administrativa é alterada em Segurança e avisos");
            }
            applyNewPassword(usuario, dto.senhaAtual(), dto.novaSenha(), dto.confirmarNovaSenha());
        }

        usuario.setNome(dto.nome().trim());
        usuario.setSobrenome(dto.sobrenome().trim());
        usuario.setEmail(email);
        usuario.setOrigemContato(isBlank(dto.origemContato()) ? null : dto.origemContato().trim());
        usuario.setInstagramUsername(normalizeInstagram(dto.instagramUsername()));

        if (empresa != null) {
            empresa.setRazaoSocial(dto.razaoSocial().trim());
            empresa.setNomeFantasia(isBlank(dto.nomeFantasia()) ? null : dto.nomeFantasia().trim());
            empresa.setTelefone(dto.telefone().trim());
        } else {
            usuario.setDataNascimento(dto.dataNascimento());
            usuario.setTelefone(dto.telefone().trim());
        }

        log.info("Conta atualizada - id: {}{}", usuario.getId(), trocaSenha ? " (com troca de senha)" : "");
        return mapper.toResponse(usuario);
    }

    private void applyNewPassword(Usuario usuario, String senhaAtual, String novaSenha, String confirmarNovaSenha) {
        if (isBlank(senhaAtual) || isBlank(novaSenha) || isBlank(confirmarNovaSenha)) {
            throw new BusinessValidationException(
                    "Para trocar a senha, preencha a senha atual, a nova senha e a confirmação");
        }
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenhaHash())) {
            log.warn("Troca de senha negada - senha atual incorreta: id {}", usuario.getId());
            throw new BusinessValidationException("Senha atual incorreta");
        }
        if (novaSenha.length() < 8 || novaSenha.length() > 72) {
            throw new BusinessValidationException("A nova senha deve ter entre 8 e 72 caracteres");
        }
        if (!novaSenha.equals(confirmarNovaSenha)) {
            throw new BusinessValidationException("A confirmação não confere com a nova senha");
        }
        if (novaSenha.equals(senhaAtual)) {
            throw new BusinessValidationException("A nova senha deve ser diferente da atual");
        }

        usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
    }

    private Usuario loggedInUser() {
        return usuarioRepository.findById(UsuarioLogado.id())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    private String normalizeInstagram(String valor) {
        if (isBlank(valor)) {
            return null;
        }
        String username = valor.trim();
        while (username.startsWith("@")) {
            username = username.substring(1);
        }
        return username.isEmpty() ? null : username;
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.isBlank();
    }
}
package br.com.bw.backend.repository;

import br.com.bw.backend.entity.Usuario;
import br.com.bw.backend.entity.enums.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByInstagramId(String instagramId);

    Optional<Usuario> findByEmail(String email);

    boolean existsByInstagramId(String instagramId);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    List<Usuario> findByEmpresaIdAndPerfilOrderByIdAsc(Integer empresaId, Perfil perfil);

    List<Usuario> findByPerfilAndAtivoTrue(Perfil perfil);
}

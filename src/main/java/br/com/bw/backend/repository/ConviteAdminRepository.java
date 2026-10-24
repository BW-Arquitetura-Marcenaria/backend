package br.com.bw.backend.repository;

import br.com.bw.backend.entity.ConviteAdmin;
import br.com.bw.backend.entity.enums.StatusConvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConviteAdminRepository extends JpaRepository<ConviteAdmin, Integer> {

    Optional<ConviteAdmin> findByTokenHash(String tokenHash);

    boolean existsByEmailAndStatus(String email, StatusConvite status);

    boolean existsByCpfAndStatus(String cpf, StatusConvite status);

    List<ConviteAdmin> findByEmpresaIdAndStatusOrderByCriadoEmDesc(Integer empresaId, StatusConvite status);

    List<ConviteAdmin> findByStatusAndExpiraEmBefore(StatusConvite status, LocalDateTime limite);
}
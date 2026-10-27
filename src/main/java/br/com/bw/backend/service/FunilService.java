package br.com.bw.backend.service;

import br.com.bw.backend.dto.request.FunilEtapaPutRequestDTO;
import br.com.bw.backend.dto.response.FunilCardGetResponseDTO;
import br.com.bw.backend.dto.response.FunilColunaGetResponseDTO;
import br.com.bw.backend.dto.response.FunilDetalheGetResponseDTO;
import br.com.bw.backend.dto.response.FunilGetResponseDTO;
import br.com.bw.backend.entity.LeadInstagram;
import br.com.bw.backend.entity.Usuario;
import br.com.bw.backend.entity.enums.EtapaFunil;
import br.com.bw.backend.entity.enums.Perfil;
import br.com.bw.backend.exception.BusinessValidationException;
import br.com.bw.backend.repository.LeadInstagramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class FunilService {

    private static final String ORIGEM = "Instagram";

    private static final List<EtapaFunil> ETAPAS_DO_BOARD = List.of(
            EtapaFunil.QUALIFICADO,
            EtapaFunil.ORCAMENTO_ENVIADO,
            EtapaFunil.EM_NEGOCIACAO,
            EtapaFunil.FECHADO);

    private final LeadInstagramRepository leadRepository;

    @Transactional(readOnly = true)
    public FunilGetResponseDTO listar() {
        List<LeadInstagram> leads = leadRepository.findParaFunil(Perfil.CLIENTE, ETAPAS_DO_BOARD);

        Map<EtapaFunil, List<FunilCardGetResponseDTO>> porEtapa = new EnumMap<>(EtapaFunil.class);
        ETAPAS_DO_BOARD.forEach(etapa -> porEtapa.put(etapa, new ArrayList<>()));

        BigDecimal pipeline = BigDecimal.ZERO;
        long ativas = 0;
        long somaScore = 0;
        long comScore = 0;

        for (LeadInstagram lead : leads) {
            EtapaFunil etapa = lead.getUsuario().getEtapaFunil();
            porEtapa.get(etapa).add(paraCard(lead));

            if (etapa != EtapaFunil.FECHADO) {
                ativas++;
                if (lead.getOrcamento() != null) {
                    pipeline = pipeline.add(lead.getOrcamento());
                }
                if (lead.getScore() != null) {
                    somaScore += lead.getScore();
                    comScore++;
                }
            }
        }

        int scoreMedio = comScore == 0 ? 0 : (int) Math.round((double) somaScore / comScore);

        List<FunilColunaGetResponseDTO> colunas = ETAPAS_DO_BOARD.stream()
                .map(etapa -> new FunilColunaGetResponseDTO(
                        etapa,
                        titulo(etapa),
                        porEtapa.get(etapa).size(),
                        porEtapa.get(etapa)))
                .toList();

        return new FunilGetResponseDTO(ativas, pipeline, scoreMedio, colunas);
    }

    @Transactional(readOnly = true)
    public FunilDetalheGetResponseDTO detalhar(Integer clienteId) {
        return paraDetalhe(leadDoFunil(clienteId));
    }

    @Transactional
    public FunilDetalheGetResponseDTO alterarEtapa(Integer adminId, Integer clienteId, FunilEtapaPutRequestDTO dto) {
        if (!ETAPAS_DO_BOARD.contains(dto.etapa())) {
            throw new BusinessValidationException("Etapa inválida para o funil");
        }

        LeadInstagram lead = leadDoFunil(clienteId);
        EtapaFunil anterior = lead.getUsuario().getEtapaFunil();
        lead.getUsuario().setEtapaFunil(dto.etapa());

        log.info("Etapa do funil alterada - cliente: {} de: {} para: {} por: {}",
                clienteId, anterior, dto.etapa(), adminId);
        return paraDetalhe(lead);
    }

    // ---------- Internos ----------

    private LeadInstagram leadDoFunil(Integer clienteId) {
        return leadRepository.findByUsuarioId(clienteId)
                .filter(l -> l.getUsuario().getPerfil() == Perfil.CLIENTE)
                .filter(l -> ETAPAS_DO_BOARD.contains(l.getUsuario().getEtapaFunil()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Oportunidade não encontrada"));
    }

    private FunilCardGetResponseDTO paraCard(LeadInstagram lead) {
        Usuario usuario = lead.getUsuario();
        return new FunilCardGetResponseDTO(
                usuario.getId(),
                nomeCompleto(usuario),
                iniciais(usuario),
                ORIGEM,
                lead.getCriadoEm(),
                tipoProjeto(lead),
                lead.getOrcamento(),
                lead.getScore(),
                lead.getClassificacao());
    }

    private FunilDetalheGetResponseDTO paraDetalhe(LeadInstagram lead) {
        Usuario usuario = lead.getUsuario();
        return new FunilDetalheGetResponseDTO(
                usuario.getId(),
                nomeCompleto(usuario),
                iniciais(usuario),
                tipoProjeto(lead),
                lead.getOrcamento(),
                ORIGEM,
                lead.getCriadoEm(),
                lead.getScore(),
                lead.getClassificacao(),
                usuario.getEtapaFunil());
    }

    private String titulo(EtapaFunil etapa) {
        return switch (etapa) {
            case QUALIFICADO -> "Qualificado";
            case ORCAMENTO_ENVIADO -> "Orçamento Enviado";
            case EM_NEGOCIACAO -> "Em Negociação";
            case FECHADO -> "Fechado";
            default -> etapa.getValor();
        };
    }

    private String tipoProjeto(LeadInstagram lead) {
        boolean temMovel = lead.getMovel() != null && !lead.getMovel().isBlank();
        boolean temAmbiente = lead.getAmbiente() != null && !lead.getAmbiente().isBlank();

        if (temMovel && temAmbiente) {
            return lead.getMovel().trim() + " · " + lead.getAmbiente().trim();
        }
        if (temMovel) {
            return lead.getMovel().trim();
        }
        return temAmbiente ? lead.getAmbiente().trim() : null;
    }

    private String nomeCompleto(Usuario u) {
        String sobrenome = u.getSobrenome() == null ? "" : u.getSobrenome();
        return (u.getNome() + " " + sobrenome).trim();
    }

    private String iniciais(Usuario u) {
        String primeira = u.getNome().isBlank() ? "" : u.getNome().substring(0, 1);
        String segunda = (u.getSobrenome() == null || u.getSobrenome().isBlank()) ? "" : u.getSobrenome().substring(0, 1);
        return (primeira + segunda).toUpperCase();
    }
}
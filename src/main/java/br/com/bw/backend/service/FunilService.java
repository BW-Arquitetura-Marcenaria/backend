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
import br.com.bw.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FunilService {

    private static final List<EtapaFunil> ETAPAS_DO_BOARD = List.of(
            EtapaFunil.NOVO_LEAD,
            EtapaFunil.EM_QUALIFICACAO,
            EtapaFunil.QUALIFICADO,
            EtapaFunil.ORCAMENTO_ENVIADO,
            EtapaFunil.EM_NEGOCIACAO,
            EtapaFunil.FECHADO);

    private final LeadInstagramRepository leadRepository;
    private final UsuarioRepository usuarioRepository;

    private record Item(Usuario usuario, LeadInstagram lead) {}

    @Transactional(readOnly = true)
    public FunilGetResponseDTO findAll(String origemFiltro) {
        String filtro = normalizeOrigin(origemFiltro);

        List<Item> itens = leadRepository.findClientesComLead(Perfil.CLIENTE).stream()
                .map(linha -> new Item((Usuario) linha[0], (LeadInstagram) linha[1]))
                .filter(item -> filtro == null || filtro.equals(getOrigin(item)))
                .toList();

        Map<EtapaFunil, List<FunilCardGetResponseDTO>> porEtapa = new EnumMap<>(EtapaFunil.class);
        ETAPAS_DO_BOARD.forEach(etapa -> porEtapa.put(etapa, new ArrayList<>()));

        BigDecimal pipeline = BigDecimal.ZERO;
        long ativas = 0;
        long somaScore = 0;
        long comScore = 0;

        for (Item item : itens) {
            EtapaFunil etapa = getStage(item.usuario());
            porEtapa.get(etapa).add(toCard(item));

            if (etapa != EtapaFunil.FECHADO) {
                ativas++;
                LeadInstagram lead = item.lead();
                if (lead != null && lead.getOrcamento() != null) {
                    pipeline = pipeline.add(lead.getOrcamento());
                }
                if (lead != null && lead.getScore() != null) {
                    somaScore += lead.getScore();
                    comScore++;
                }
            }
        }

        int scoreMedio = comScore == 0 ? 0 : (int) Math.round((double) somaScore / comScore);

        List<FunilColunaGetResponseDTO> colunas = ETAPAS_DO_BOARD.stream()
                .map(etapa -> new FunilColunaGetResponseDTO(
                        etapa,
                        getStageTitle(etapa),
                        porEtapa.get(etapa).size(),
                        porEtapa.get(etapa)))
                .toList();

        return new FunilGetResponseDTO(ativas, pipeline, scoreMedio, colunas);
    }

    @Transactional(readOnly = true)
    public FunilDetalheGetResponseDTO findDetailsById(Integer clienteId) {
        return toDetails(findItemForFunnel(clienteId));
    }

    @Transactional
    public FunilDetalheGetResponseDTO updateStage(Integer adminId, Integer clienteId, FunilEtapaPutRequestDTO dto) {
        if (!ETAPAS_DO_BOARD.contains(dto.etapa())) {
            throw new BusinessValidationException("Etapa inválida para o funil");
        }

        Item item = findItemForFunnel(clienteId);
        EtapaFunil anterior = getStage(item.usuario());
        item.usuario().setEtapaFunil(dto.etapa());

        log.info("Etapa do funil alterada - cliente: {} de: {} para: {} por: {}",
                clienteId, anterior, dto.etapa(), adminId);
        return toDetails(item);
    }

    private Item findItemForFunnel(Integer clienteId) {
        Usuario usuario = usuarioRepository.findById(clienteId)
                .filter(u -> u.getPerfil() == Perfil.CLIENTE)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Oportunidade não encontrada"));
        LeadInstagram lead = leadRepository.findByUsuarioId(clienteId).orElse(null);
        return new Item(usuario, lead);
    }

    private String normalizeOrigin(String origem) {
        if (origem == null || origem.isBlank() || origem.equalsIgnoreCase("todos")) {
            return null;
        }
        if (origem.equalsIgnoreCase(LeadInstagram.ORIGEM_INSTAGRAM)) {
            return LeadInstagram.ORIGEM_INSTAGRAM;
        }
        if (origem.equalsIgnoreCase(LeadInstagram.ORIGEM_PLATAFORMA)) {
            return LeadInstagram.ORIGEM_PLATAFORMA;
        }
        throw new BusinessValidationException("Origem inválida. Use Todos, Instagram ou Plataforma");
    }

    private String getOrigin(Item item) {
        if (item.lead() != null && item.lead().getOrigem() != null) {
            return item.lead().getOrigem();
        }
        String instagramId = item.usuario().getInstagramId();
        return instagramId != null && !instagramId.isBlank()
                ? LeadInstagram.ORIGEM_INSTAGRAM
                : LeadInstagram.ORIGEM_PLATAFORMA;
    }

    private EtapaFunil getStage(Usuario usuario) {
        return usuario.getEtapaFunil() == null ? EtapaFunil.NOVO_LEAD : usuario.getEtapaFunil();
    }

    private LocalDateTime getReceivedAt(Item item) {
        LeadInstagram lead = item.lead();
        boolean instagram = LeadInstagram.ORIGEM_INSTAGRAM.equals(getOrigin(item));
        if (instagram && lead != null && lead.getCriadoEm() != null) {
            return lead.getCriadoEm();
        }
        if (item.usuario().getDataCadastro() != null) {
            return item.usuario().getDataCadastro();
        }
        return lead == null ? null : lead.getCriadoEm();
    }

    private FunilCardGetResponseDTO toCard(Item item) {
        Usuario usuario = item.usuario();
        LeadInstagram lead = item.lead();
        return new FunilCardGetResponseDTO(
                usuario.getId(),
                getFullName(usuario),
                getInitials(usuario),
                getOrigin(item),
                getReceivedAt(item),
                getProjectType(lead),
                lead == null ? null : lead.getOrcamento(),
                lead == null ? null : lead.getScore(),
                lead == null ? null : lead.getClassificacao());
    }

    private FunilDetalheGetResponseDTO toDetails(Item item) {
        Usuario usuario = item.usuario();
        LeadInstagram lead = item.lead();
        return new FunilDetalheGetResponseDTO(
                usuario.getId(),
                getFullName(usuario),
                getInitials(usuario),
                getProjectType(lead),
                lead == null ? null : lead.getOrcamento(),
                getOrigin(item),
                getReceivedAt(item),
                lead == null ? null : lead.getScore(),
                lead == null ? null : lead.getClassificacao(),
                getStage(usuario));
    }

    private String getStageTitle(EtapaFunil etapa) {
        return switch (etapa) {
            case NOVO_LEAD -> "Novo Lead";
            case EM_QUALIFICACAO -> "Em Qualificação";
            case QUALIFICADO -> "Qualificado";
            case ORCAMENTO_ENVIADO -> "Orçamento Enviado";
            case EM_NEGOCIACAO -> "Em Negociação";
            case FECHADO -> "Fechado";
        };
    }

    private String getProjectType(LeadInstagram lead) {
        if (lead == null) {
            return null;
        }
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

    private String getFullName(Usuario u) {
        String sobrenome = u.getSobrenome() == null ? "" : u.getSobrenome();
        return (u.getNome() + " " + sobrenome).trim();
    }

    private String getInitials(Usuario u) {
        String primeira = u.getNome().isBlank() ? "" : u.getNome().substring(0, 1);
        String segunda = (u.getSobrenome() == null || u.getSobrenome().isBlank()) ? "" : u.getSobrenome().substring(0, 1);
        return (primeira + segunda).toUpperCase();
    }
}
package br.com.bw.backend.service;

import br.com.bw.backend.dto.request.AssinaturaSignatarioPostRequestDTO;
import br.com.bw.backend.entity.AssinaturaContrato;
import br.com.bw.backend.entity.AssinaturaSignatario;
import br.com.bw.backend.entity.Projeto;
import br.com.bw.backend.entity.enums.StatusAssinatura;
import br.com.bw.backend.entity.enums.StatusSignatario;
import br.com.bw.backend.exception.BusinessValidationException;
import br.com.bw.backend.exception.ClicksignIntegrationException;
import br.com.bw.backend.exception.NotFoundException;
import br.com.bw.backend.repository.AssinaturaContratoRepository;
import br.com.bw.backend.repository.AssinaturaSignatarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssinaturaContratoService {

    private final AssinaturaContratoRepository assinaturaContratoRepository;
    private final AssinaturaSignatarioRepository assinaturaSignatarioRepository;
    private final ProjetoService projetoService;
    private final ClicksignClient clicksignClient;

    @Transactional
    public AssinaturaContrato sendForSignature(
            Integer projetoId, MultipartFile arquivo, List<AssinaturaSignatarioPostRequestDTO> signatarios) {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new BusinessValidationException("O arquivo do contrato é obrigatório.");
        }
        if (signatarios == null || signatarios.isEmpty()) {
            throw new BusinessValidationException("É necessário informar ao menos um signatário.");
        }

        Projeto projeto = projetoService.findById(projetoId);

        String nomeArquivo = arquivo.getOriginalFilename() != null
                ? arquivo.getOriginalFilename()
                : "contrato-" + projetoId + ".pdf";

        String conteudoBase64 = encodeToBase64(arquivo, nomeArquivo);

        log.info("Enviando contrato do projeto {} para assinatura na Clicksign", projetoId);

        String envelopeId = clicksignClient.createEnvelope(
                "Contrato - " + projeto.getTituloProjeto() + " (#" + projetoId + ")");
        String documentId = clicksignClient.addDocument(envelopeId, nomeArquivo, conteudoBase64);

        AssinaturaContrato assinatura = AssinaturaContrato.builder()
                .projeto(projeto)
                .envelopeId(envelopeId)
                .documentId(documentId)
                .nomeArquivo(nomeArquivo)
                .status(StatusAssinatura.RASCUNHO)
                .build();

        for (AssinaturaSignatarioPostRequestDTO signatarioDTO : signatarios) {
            String signerId = clicksignClient.addSigner(
                    envelopeId,
                    signatarioDTO.nome(),
                    signatarioDTO.email(),
                    signatarioDTO.telefone(),
                    signatarioDTO.cpf());

            clicksignClient.addQualificationRequirement(envelopeId, documentId, signerId);
            clicksignClient.addAuthenticationRequirement(envelopeId, documentId, signerId);

            assinatura.getSignatarios().add(AssinaturaSignatario.builder()
                    .assinatura(assinatura)
                    .signerId(signerId)
                    .nome(signatarioDTO.nome())
                    .email(signatarioDTO.email())
                    .cpf(signatarioDTO.cpf())
                    .status(StatusSignatario.PENDENTE)
                    .build());
        }

        clicksignClient.activateEnvelope(envelopeId);
        assinatura.setStatus(StatusAssinatura.ENVIADO);

        AssinaturaContrato salva = assinaturaContratoRepository.save(assinatura);
        log.info("Contrato do projeto {} enviado com sucesso - envelopeId: {}", projetoId, envelopeId);
        return salva;
    }

    public List<AssinaturaContrato> listByProjectId(Integer projetoId) {
        projetoService.findById(projetoId);
        return assinaturaContratoRepository.findByProjetoId(projetoId);
    }

    public AssinaturaContrato findByProjectAndId(Integer projetoId, Integer id) {
        projetoService.findById(projetoId);
        return assinaturaContratoRepository.findByIdAndProjetoId(id, projetoId)
                .orElseThrow(() -> new NotFoundException("Assinatura não encontrada"));
    }

    @Transactional
    public void processWebhookEvent(String documentId, String eventoClicksign, String signerId) {
        if (documentId == null || documentId.isBlank()) {
            log.warn("Webhook da Clicksign recebido sem document.key - evento: {}", eventoClicksign);
            return;
        }

        AssinaturaContrato assinatura = assinaturaContratoRepository.findByDocumentId(documentId).orElse(null);
        if (assinatura == null) {
            log.warn("Webhook da Clicksign recebido para documento desconhecido: {}", documentId);
            return;
        }

        String evento = eventoClicksign == null ? "" : eventoClicksign.toLowerCase(Locale.ROOT);

        if (signerId != null && !signerId.isBlank() && evento.contains("sign")) {
            assinaturaSignatarioRepository.findBySignerId(signerId)
                    .ifPresent(signatario -> signatario.setStatus(StatusSignatario.ASSINADO));
        }

        switch (evento) {
            case "auto_close", "document_closed", "close" -> assinatura.setStatus(StatusAssinatura.CONCLUIDO);
            case "cancel", "deadline", "envelope_canceled" -> assinatura.setStatus(StatusAssinatura.CANCELADO);
            case "refusal", "sign_refused" -> assinatura.setStatus(StatusAssinatura.RECUSADO);
            default -> log.info("Evento da Clicksign não mapeado para alteração de status: {}", eventoClicksign);
        }

        assinaturaContratoRepository.save(assinatura);
    }

    private String encodeToBase64(MultipartFile arquivo, String nomeArquivo) {
        String mimeType = arquivo.getContentType() != null ? arquivo.getContentType() : "application/pdf";
        try {
            String base64 = Base64.getEncoder().encodeToString(arquivo.getBytes());
            return "data:" + mimeType + ";base64," + base64;
        } catch (IOException e) {
            throw new ClicksignIntegrationException("Falha ao ler o arquivo " + nomeArquivo, e);
        }
    }
}

package br.com.bw.backend.controller;

import br.com.bw.backend.config.ClicksignProperties;
import br.com.bw.backend.service.AssinaturaContratoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/v1/api/webhooks/clicksign")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Webhook Clicksign", description = "Recebe notificações de eventos de assinatura da Clicksign")
public class ClicksignWebhookController {

    private static final String HEADER_HMAC = "Content-Hmac";

    private final AssinaturaContratoService assinaturaContratoService;
    private final ClicksignProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping
    @Operation(
            summary = "Receber evento de webhook da Clicksign",
            description = "Endpoint público chamado pela Clicksign a cada evento do envelope/documento "
                    + "(ex.: sign, auto_close, cancel, refusal). A autenticidade é validada pelo "
                    + "cabeçalho Content-Hmac (HMAC SHA-256 do corpo com o secret do webhook).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento processado"),
            @ApiResponse(responseCode = "401", description = "Assinatura HMAC ausente ou inválida")
    })
    public ResponseEntity<Void> receive(
            @RequestBody String corpo,
            @RequestHeader(value = HEADER_HMAC, required = false) String assinaturaHmac,
            HttpServletRequest request) {

        if (!isSignatureValid(corpo, assinaturaHmac)) {
            log.warn("Webhook da Clicksign com assinatura HMAC inválida recebido de {}", request.getRemoteAddr());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            JsonNode raiz = objectMapper.readTree(corpo);
            String nomeEvento = raiz.path("event").path("name").asText(null);
            String documentId = raiz.path("document").path("key").asText(null);
            String signerId = raiz.path("event").path("data").path("signer").path("key").asText(null);

            assinaturaContratoService.processWebhookEvent(documentId, nomeEvento, signerId);
        } catch (Exception e) {
            log.error("Falha ao processar webhook da Clicksign", e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        return ResponseEntity.ok().build();
    }

    private boolean isSignatureValid(String corpo, String assinaturaRecebida) {
        String secret = properties.getWebhookSecret();
        if (secret == null || secret.isBlank()) {
            return false;
        }
        if (assinaturaRecebida == null || assinaturaRecebida.isBlank()) {
            return false;
        }

        String esperada = computeHmac(corpo, secret);
        String recebida = assinaturaRecebida.startsWith("sha256=")
                ? assinaturaRecebida.substring("sha256=".length())
                : assinaturaRecebida;

        return MessageDigest.isEqual(
                esperada.getBytes(StandardCharsets.UTF_8),
                recebida.getBytes(StandardCharsets.UTF_8));
    }

    private String computeHmac(String corpo, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(corpo.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao calcular HMAC do webhook da Clicksign", e);
        }
    }
}

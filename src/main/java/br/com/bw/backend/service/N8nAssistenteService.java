package br.com.bw.backend.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Service
@Slf4j
public class N8nAssistenteService {

    private static final String HEADER_API_KEY = "X-Api-Key";

    @Value("${app.n8n.webhook-url}")
    private String webhookUrl;

    @Value("${app.n8n.api-key}")
    private String apiKey;

    @Value("${app.n8n.timeout-segundos:90}")
    private long timeoutSegundos;

    private RestClient restClient;

    public record Requisicao(Integer usuarioId, String mensagem, String nome, String email) {}

    public record Resposta(String resposta, Boolean concluido) {}

    @PostConstruct
    void init() {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(timeoutSegundos));
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    public Resposta send(Integer usuarioId, String nome, String email, String mensagem) {
        try {
            Resposta resposta = restClient.post()
                    .uri(webhookUrl)
                    .header(HEADER_API_KEY, apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Requisicao(usuarioId, mensagem, nome, email))
                    .retrieve()
                    .body(Resposta.class);

            if (resposta == null || resposta.resposta() == null || resposta.resposta().isBlank()) {
                log.error("Resposta vazia do assistente (n8n) - usuarioId: {}", usuarioId);
                throw unavailable();
            }
            return resposta;
        } catch (RestClientException e) {
            log.error("Falha ao chamar o assistente (n8n) - usuarioId: {}", usuarioId, e);
            throw unavailable();
        }
    }

    private ResponseStatusException unavailable() {
        return new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                "O assistente está indisponível no momento. Tente novamente em instantes.");
    }
}
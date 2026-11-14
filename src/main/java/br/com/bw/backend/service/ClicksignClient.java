package br.com.bw.backend.service;

import br.com.bw.backend.config.ClicksignProperties;
import br.com.bw.backend.exception.ClicksignIntegrationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClicksignClient {

    private static final MediaType JSON_API = MediaType.parseMediaType("application/vnd.api+json");

    private final ClicksignProperties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String createEnvelope(String name) {
        ObjectNode body = resource("envelopes", objectMapper.createObjectNode().put("name", name));
        JsonNode response = post("/envelopes", body, "create envelope");
        return extractId(response);
    }

    public String addDocument(String envelopeId, String filename, String base64Content) {
        ObjectNode attributes = objectMapper.createObjectNode()
                .put("filename", filename)
                .put("content_base64", base64Content);

        JsonNode response = post(
                "/envelopes/%s/documents".formatted(envelopeId),
                resource("documents", attributes),
                "add document to envelope " + envelopeId);

        return extractId(response);
    }

    public String addSigner(String envelopeId, String name, String email, String phoneNumber, String cpf) {
        ObjectNode attributes = objectMapper.createObjectNode()
                .put("name", name)
                .put("email", email)
                .put("has_documentation", cpf != null && !cpf.isBlank());

        if (phoneNumber != null && !phoneNumber.isBlank()) {
            attributes.put("phone_number", phoneNumber);
        }
        if (cpf != null && !cpf.isBlank()) {
            attributes.put("documentation", cpf);
        }

        JsonNode response = post(
                "/envelopes/%s/signers".formatted(envelopeId),
                resource("signers", attributes),
                "add signer to envelope " + envelopeId);

        return extractId(response);
    }

    public void addQualificationRequirement(String envelopeId, String documentId, String signerId) {
        ObjectNode attributes = objectMapper.createObjectNode()
                .put("action", "agree")
                .put("role", "sign");

        addRequirement(envelopeId, documentId, signerId, attributes);
    }

    public void addAuthenticationRequirement(String envelopeId, String documentId, String signerId) {
        ObjectNode attributes = objectMapper.createObjectNode()
                .put("action", "provide_evidence")
                .put("auth", properties.getAuthMethod());

        addRequirement(envelopeId, documentId, signerId, attributes);
    }

    public void activateEnvelope(String envelopeId) {
        ObjectNode data = objectMapper.createObjectNode()
                .put("id", envelopeId)
                .put("type", "envelopes");
        data.set("attributes", objectMapper.createObjectNode().put("status", "running"));

        patch("/envelopes/%s".formatted(envelopeId), wrap(data), "activate envelope " + envelopeId);
    }

    private void addRequirement(String envelopeId, String documentId, String signerId, ObjectNode attributes) {
        ObjectNode data = objectMapper.createObjectNode().put("type", "requirements");
        data.set("attributes", attributes);
        data.set("relationships", relationships(documentId, signerId));

        post("/envelopes/%s/requirements".formatted(envelopeId), wrap(data), "add requirement to envelope " + envelopeId);
    }

    private ObjectNode relationships(String documentId, String signerId) {
        ObjectNode relationships = objectMapper.createObjectNode();
        relationships.set("document", wrap(reference("documents", documentId)));
        relationships.set("signer", wrap(reference("signers", signerId)));
        return relationships;
    }

    private ObjectNode reference(String type, String id) {
        return objectMapper.createObjectNode().put("type", type).put("id", id);
    }

    private ObjectNode resource(String type, ObjectNode attributes) {
        ObjectNode data = objectMapper.createObjectNode().put("type", type);
        data.set("attributes", attributes);
        return wrap(data);
    }

    private ObjectNode wrap(ObjectNode data) {
        ObjectNode body = objectMapper.createObjectNode();
        body.set("data", data);
        return body;
    }

    private String extractId(JsonNode response) {
        JsonNode id = response.path("data").path("id");
        if (id.isMissingNode() || id.asText().isBlank()) {
            throw new ClicksignIntegrationException("Clicksign response did not return a valid id: " + response);
        }
        return id.asText();
    }

    private JsonNode post(String uri, ObjectNode body, String operation) {
        return execute(() -> restClient().post().uri(uri).body(body).retrieve().body(JsonNode.class), operation);
    }

    private JsonNode patch(String uri, ObjectNode body, String operation) {
        return execute(() -> restClient().patch().uri(uri).body(body).retrieve().body(JsonNode.class), operation);
    }

    private JsonNode execute(Supplier<JsonNode> call, String operation) {
        try {
            return call.get();
        } catch (RestClientResponseException e) {
            log.error("Clicksign call failed - operation: {}, status: {}, body: {}",
                    operation, e.getStatusCode(), e.getResponseBodyAsString());
            throw new ClicksignIntegrationException(
                    "Failed to " + operation + " on Clicksign (status " + e.getStatusCode() + ")", e);
        } catch (Exception e) {
            log.error("Clicksign call failed - operation: {}", operation, e);
            throw new ClicksignIntegrationException("Failed to " + operation + " on Clicksign", e);
        }
    }

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl() + "/api/v3")
                .defaultHeader(HttpHeaders.AUTHORIZATION, properties.getAccessToken())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, JSON_API.toString())
                .defaultHeader(HttpHeaders.ACCEPT, JSON_API.toString())
                .build();
    }
}

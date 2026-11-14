package br.com.bw.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "integracoes.clicksign")
@Getter
@Setter
public class ClicksignProperties {
    private String baseUrl = "https://sandbox.clicksign.com";
    private String accessToken;
    private String webhookSecret;
    private String authMethod = "email";
}

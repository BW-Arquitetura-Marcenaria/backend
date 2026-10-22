
package br.com.bw.backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "integracoes.google.calendar")
@Getter
@Setter
public class GoogleCalendarProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
    private String scope;
}

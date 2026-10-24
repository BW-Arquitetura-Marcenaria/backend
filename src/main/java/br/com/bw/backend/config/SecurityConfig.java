package br.com.bw.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Value("${app.integracao.api-key}")
    private String apiKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/v1/api/cadastro/**", "/v1/api/auth/**", "/error").permitAll()
                        .requestMatchers("/v1/api/convite-admin/**").permitAll()

                        .requestMatchers("/v1/api/integracoes/**").hasRole("INTEGRACAO")

                        .requestMatchers("/v1/rastreabilidades/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/v1/projetos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/v1/projetos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/v1/projetos/**").hasRole("ADMIN")
                        .requestMatchers("/v1/admin/**").hasRole("ADMIN")

                        .requestMatchers("/v1/chat/**").hasRole("CLIENTE")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(new ApiKeyFilter(apiKey), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
package com.animecom.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

        @Value("${spring.security.oauth2.resourceserver.jwt.secret-key}")
        private String jwtSecret;

        /**
         * Builds a ReactiveJwtDecoder using the HMAC-SHA256 symmetric secret key
         * configured via {@code spring.security.oauth2.resourceserver.jwt.secret-key}.
         * Spring Boot's auto-configuration only supports issuer-uri / jwk-set-uri out
         * of the box; for a symmetric secret we must register the decoder manually.
         */
        @Bean
        public ReactiveJwtDecoder reactiveJwtDecoder() {
                byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
                SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
                return NimbusReactiveJwtDecoder.withSecretKey(secretKey).build();
        }

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                        ReactiveJwtDecoder jwtDecoder) {
                return http
                                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                                .authorizeExchange(exchanges -> exchanges
                                                // Public endpoints — no auth required
                                                .pathMatchers(
                                                                "/api/auth/register",
                                                                "/api/auth/login",
                                                                "/api/auth/refresh",
                                                                "/actuator/health",
                                                                "/actuator/info",
                                                                "/fallback/**")
                                                .permitAll()
                                                // Everything else requires authentication
                                                .anyExchange().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> jwt.jwtDecoder(jwtDecoder)))
                                .build();
        }
}

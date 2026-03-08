package com.animecom.gateway.config;

import org.springframework.context.annotation.Configuration;

/**
 * CORS is handled globally by Spring Cloud Gateway via
 * {@code spring.cloud.gateway.server.webflux.globalcors} in application.yml.
 *
 * A separate CorsWebFilter bean is intentionally omitted to avoid duplicate
 * Access-Control-Allow-* headers being added by both the gateway CORS filter
 * and a custom bean.
 */
@Configuration
public class CorsConfig {
    // No bean needed — gateway globalcors config in application.yml handles CORS.
}

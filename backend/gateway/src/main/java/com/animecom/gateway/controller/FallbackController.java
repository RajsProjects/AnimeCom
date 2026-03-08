package com.animecom.gateway.controller;

import com.animecom.gateway.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Fallback controller invoked when a downstream microservice circuit breaker
 * trips.
 * Returns Mono<ResponseEntity> as required by Spring WebFlux.
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public Mono<ResponseEntity<ErrorResponse>> authFallback() {
        return buildFallback("Auth Service is currently unavailable", "/fallback/auth");
    }

    @GetMapping("/chat")
    public Mono<ResponseEntity<ErrorResponse>> chatFallback() {
        return buildFallback("Chat Service is currently unavailable", "/fallback/chat");
    }

    @GetMapping("/notifications")
    public Mono<ResponseEntity<ErrorResponse>> notificationsFallback() {
        return buildFallback("Notification Service is currently unavailable", "/fallback/notifications");
    }

    @GetMapping("/presence")
    public Mono<ResponseEntity<ErrorResponse>> presenceFallback() {
        return buildFallback("Presence Service is currently unavailable", "/fallback/presence");
    }

    private Mono<ResponseEntity<ErrorResponse>> buildFallback(String message, String path) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                message,
                path,
                Instant.now().toString());
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body));
    }
}

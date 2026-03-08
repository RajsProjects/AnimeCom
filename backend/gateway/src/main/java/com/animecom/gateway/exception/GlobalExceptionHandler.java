package com.animecom.gateway.exception;

import com.animecom.gateway.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Centralized exception handler that returns consistent JSON error responses.
 */
@Slf4j
@Order(-2) // Run before Spring's default error handler
@Component
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = determineStatus(ex);
        String message = determineMessage(ex, status);
        String path = exchange.getRequest().getURI().getPath();

        log.error("Gateway error on {} — {} : {}", path, status.value(), message, ex);

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                Instant.now().toString());

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] bytes = toJson(errorResponse);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory()
                        .wrap(bytes)));
    }

    private HttpStatus determineStatus(Throwable ex) {
        if (ex instanceof ResponseStatusException rse) {
            return HttpStatus.valueOf(rse.getStatusCode().value());
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String determineMessage(Throwable ex, HttpStatus status) {
        if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
            return "An unexpected error occurred";
        }
        return ex.getMessage();
    }

    private byte[] toJson(ErrorResponse error) {
        try {
            return objectMapper.writeValueAsBytes(error);
        } catch (Exception e) {
            return ("{\"status\":" + error.status() + ",\"error\":\"" + error.error() + "\"}").getBytes();
        }
    }
}

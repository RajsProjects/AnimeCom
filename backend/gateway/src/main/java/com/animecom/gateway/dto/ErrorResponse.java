package com.animecom.gateway.dto;

/**
 * Standard error response DTO returned by the gateway on failures.
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        String timestamp
) {
}

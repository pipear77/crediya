package co.com.pragma.api.dto;

public record ErrorResponseDTO(
        int status,
        String error,
        String message,
        String path
) {}
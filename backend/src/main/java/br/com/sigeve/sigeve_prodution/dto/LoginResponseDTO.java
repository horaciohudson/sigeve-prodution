package br.com.sigeve.sigeve_prodution.dto;




public record LoginResponseDTO(
        String accessToken,
        String refreshToken,
        String tokenType,
        long   expiresIn
) {}
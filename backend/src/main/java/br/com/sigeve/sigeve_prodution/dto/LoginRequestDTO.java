package br.com.sigeve.sigeve_prodution.dto;



import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String tenantCode   // ou tenantId se preferir
) {}
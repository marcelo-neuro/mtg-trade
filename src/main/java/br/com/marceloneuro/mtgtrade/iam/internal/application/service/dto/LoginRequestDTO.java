package br.com.marceloneuro.mtgtrade.iam.internal.application.service.dto;

public record LoginRequestDTO(
        String email,
        String senha
) {
}

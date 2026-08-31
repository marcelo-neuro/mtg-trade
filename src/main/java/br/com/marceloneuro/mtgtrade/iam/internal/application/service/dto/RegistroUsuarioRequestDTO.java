package br.com.marceloneuro.mtgtrade.iam.internal.application.service.dto;

public record RegistroUsuarioRequestDTO(
        String nomeUsuario,
        String email,
        String senha
) {
}

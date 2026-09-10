package br.com.marceloneuro.mtgtrade.iam.internal.application.service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroUsuarioRequestDTO(
        @NotBlank(message = "O nome de usuário é obrigatório.")
        @Size(min = 3, max = 50, message = "O nome de usuário deve ter entre 3 e 50 caracteres.")
        String nomeUsuario,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O formato do e-mail é inválido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 8, max = 128, message = "A senha deve ter no mínimo 8 caracteres.")
        String senha
) {
}
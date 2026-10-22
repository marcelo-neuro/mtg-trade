package br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto;

import jakarta.validation.constraints.NotBlank;

// Lista de importação padrão Moxfield
public record ListaImportacaoRequestDTO(
        @NotBlank(message = "O texto de importação não pode estar vazio.")
        String lista
) {
}

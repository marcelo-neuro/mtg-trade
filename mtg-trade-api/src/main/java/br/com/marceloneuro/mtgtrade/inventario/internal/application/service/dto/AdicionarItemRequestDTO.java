package br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdicionarItemRequestDTO(
        @NotBlank(message = "O ID da carta do catálogo é obrigatório.")
        String cartaCatalogoId,

        @NotBlank(message = "O acabamento da carta é obrigatório.")
        String acabamento,

        @NotBlank(message = "O tipo de promo é obrigatório.")
        String promo,

        @NotNull(message = "A quantidade é obrigatória.")
        @Min(value = 1, message = "A quantidade mínima para adicionar é 1.")
        Integer quantidade,

        @NotBlank(message = "O estado de conservação da carta é obrigatório.")
        String estado,

        @NotBlank(message = "O idioma da carta é obrigatório.")
        String idioma
) implements AtributosFisicosRequest{
}

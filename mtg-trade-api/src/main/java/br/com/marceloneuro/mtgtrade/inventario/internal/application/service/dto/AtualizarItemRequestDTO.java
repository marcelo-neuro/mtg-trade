package br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizarItemRequestDTO(
        @NotBlank(message = "O acabamento da carta é obrigatório.")
        String acabamento,

        @NotBlank(message = "O tipo de promo é obrigatório.")
        String promo,

        @NotNull(message = "A quantidade é obrigatória.")
        @Min(value = 1, message = "A quantidade não pode ser negativa.")
        Integer quantidade,

        @NotBlank(message = "O estado de conservação da carta é obrigatório.")
        String estado,

        @NotBlank(message = "O idioma da carta é obrigatório.")
        String idioma
) implements AtributosFisicosRequest {
}

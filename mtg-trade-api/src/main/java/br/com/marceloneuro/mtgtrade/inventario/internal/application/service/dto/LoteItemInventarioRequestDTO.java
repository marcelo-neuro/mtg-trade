package br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record LoteItemInventarioRequestDTO(

        @NotEmpty(message = "A lista de cartas não pode estar vazia.")
        List<@Valid AdicionarItemRequestDTO> lista
) {
}

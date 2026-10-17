package br.com.marceloneuro.mtgtrade.inventario.internal.application.service.dto;

import java.util.List;

public record LoteItemInventarioResponseDTO(
        List<ItemInventarioResponseDTO> lista
) {
}

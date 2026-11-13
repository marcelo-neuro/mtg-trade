package br.com.marceloneuro.mtgtrade.inventario.api.dto;

import java.util.List;

public record LoteItemInventarioResponseDTO(
        List<ItemInventarioResponseDTO> lista
) {
}

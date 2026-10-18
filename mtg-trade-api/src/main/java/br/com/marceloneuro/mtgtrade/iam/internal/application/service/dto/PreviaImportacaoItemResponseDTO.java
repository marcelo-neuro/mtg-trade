package br.com.marceloneuro.mtgtrade.iam.internal.application.service.dto;

import java.util.List;

public record PreviaImportacaoItemResponseDTO(
        String cartaCatalogoId, // Se encontrar no banco
        String nomeCarta,
        String siglaEdicao,
        String numeroColecionador,
        Integer quantidade,
        String imagemFrenteUrl,
        List<String> acabamentosDisponiveis,
        boolean encontradaNoCatalogo
) {
}

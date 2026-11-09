package br.com.marceloneuro.mtgtrade.oferta.internal.application.service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CriaOfertaRequestDTO(
        @NotBlank(message = "O ID do item oferecido é obrigatório.")
        String itemInventarioOfertadoId,

        @NotEmpty(message = "A oferta deve conter pelo menos um item desejado em troca.")
        List<@Valid ItemDesejadoRequestDTO> itensDesejados
) {
        public record ItemDesejadoRequestDTO(
                @NotBlank(message = "O ID da carta desejada no catálogo é obrigatório.")
                String cartaCatalogoId,

                @Min(value = 1, message = "A quantidade desejada deve ser no mínimo 1.")
                Integer quantidade,

                @NotNull(message = "A lista de acabamentos não pode ser nula (envie lista vazia se aceitar qualquer um).")
                List<String> acabamentosAceitos,

                @NotNull(message = "A lista de promos não pode ser nula (envie lista vazia se aceitar qualquer um).")
                List<String> promosAceitas,
                List<String> idiomasAceitos,
                String estadoMinimo
        ) {}
}

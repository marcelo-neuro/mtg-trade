package br.com.marceloneuro.mtgtrade.shared.web.exception.dto;

import java.time.Instant;

public record ErroDTO(

        String mensagem,
        String uri,
        Integer status,
        Instant timestamp

) {
}

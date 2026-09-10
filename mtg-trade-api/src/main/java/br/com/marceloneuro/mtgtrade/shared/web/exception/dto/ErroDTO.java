package br.com.marceloneuro.mtgtrade.shared.web.exception.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Getter
@RequiredArgsConstructor
public class ErroDTO {
    private final String mensagem;
    private final String uri;
    private final Integer status;
    private final Instant timestamp;

    public String mensagem() {
        return mensagem;
    }

    public String uri() {
        return uri;
    }

    public Integer status() {
        return status;
    }

    public Instant timestamp() {
        return timestamp;
    }
}

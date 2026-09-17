package br.com.marceloneuro.mtgtrade.shared.web.exception.dto;

import lombok.Getter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ValidationErroDTO extends ErroDTO{

    @Getter
    private final List<MensagemCampo> campos = new ArrayList<>();

    public ValidationErroDTO(String mensagem, String uri, Integer status, Instant timestamp) {
        super(mensagem, uri, status, timestamp);
    }

    public void adicionarCampo(MensagemCampo campo) {
        campos.add(campo);
    }
}

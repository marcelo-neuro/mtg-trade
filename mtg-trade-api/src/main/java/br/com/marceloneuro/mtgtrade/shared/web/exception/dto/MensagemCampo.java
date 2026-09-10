package br.com.marceloneuro.mtgtrade.shared.web.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MensagemCampo {

    private String campo;
    private String mensagem;
}

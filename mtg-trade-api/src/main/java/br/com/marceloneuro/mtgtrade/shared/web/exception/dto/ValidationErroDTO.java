package br.com.marceloneuro.mtgtrade.shared.web.exception.dto;

import lombok.Getter;
import org.springframework.http.ProblemDetail;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ValidationErroDTO extends ProblemDetail {

    @Getter
    private final List<MensagemCampo> campos = new ArrayList<>();

    public ValidationErroDTO(ProblemDetail other) {
        super(other);
    }

    public void adicionarCampo(MensagemCampo campo) {
        campos.add(campo);
    }
}

package br.com.marceloneuro.mtgtrade.iam.internal.domain.exception;

import br.com.marceloneuro.mtgtrade.shared.exception.ExcecaoDominio;
import br.com.marceloneuro.mtgtrade.shared.exception.TipoErroDominio;

public class FalhaNoLoginException extends ExcecaoDominio {
    public FalhaNoLoginException(String message) {
        super(
                message,
                "Falha ao Realizar Login",
                "IAM-002",
                TipoErroDominio.FALHA_LOGIN
        );
    }
}

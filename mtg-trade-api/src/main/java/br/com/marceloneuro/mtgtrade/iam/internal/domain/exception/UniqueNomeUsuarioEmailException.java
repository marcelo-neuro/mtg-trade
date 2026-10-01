package br.com.marceloneuro.mtgtrade.iam.internal.domain.exception;

import br.com.marceloneuro.mtgtrade.shared.exception.DominioException;
import br.com.marceloneuro.mtgtrade.shared.exception.TipoErroDominio;

public class UniqueNomeUsuarioEmailException extends DominioException {

    public UniqueNomeUsuarioEmailException(String mensagem) {
        super(
                mensagem,
                "Nome de Usuário ou Email já cadastrados.",
                "IAM-001",
                TipoErroDominio.CONFLITO
        );
    }
}

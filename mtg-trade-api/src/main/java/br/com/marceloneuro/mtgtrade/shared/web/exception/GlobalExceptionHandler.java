package br.com.marceloneuro.mtgtrade.shared.web.exception;

import br.com.marceloneuro.mtgtrade.shared.exception.DominioException;
import br.com.marceloneuro.mtgtrade.shared.exception.TipoErroDominio;
import br.com.marceloneuro.mtgtrade.shared.web.exception.dto.MensagemCampo;
import br.com.marceloneuro.mtgtrade.shared.web.exception.dto.ValidationErroDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Mapeia os TipoErroDominio com os HttpStatus, para acessar os status dos erros em O(1).
    // Também delega ao Handler decidir o erro sem a lógica interna vazar para o controller.
    private static final Map<TipoErroDominio, HttpStatus> MAP_ERRO_STATUS = Map.of(
            TipoErroDominio.RECURSO_NAO_ENCONTRADO, HttpStatus.NOT_FOUND,
            TipoErroDominio.FALHA_LOGIN, HttpStatus.UNAUTHORIZED,
            TipoErroDominio.CONFLITO, HttpStatus.CONFLICT
    );

    @ExceptionHandler(DominioException.class)
    public ResponseEntity<ProblemDetail> dominioExceptionHandler(DominioException e, HttpServletRequest request) {
        HttpStatus status = MAP_ERRO_STATUS.getOrDefault(e.getErroDominio(), HttpStatus.INTERNAL_SERVER_ERROR);
        ProblemDetail erro = ProblemDetail.forStatusAndDetail(status, e.getMessage());
        erro.setTitle(e.getTitulo());
        erro.setInstance(URI.create(request.getRequestURI()));

        // O ProblemDetail permite a adição de propriedades customizadas, aqui adicionamos "código", "uri" e "timestamp"
        erro.setProperties(Map.of(
                "código", e.getCodigo(),
                "timestamp", Instant.now()
        ));

        return ResponseEntity.status(status.value()).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErroDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_CONTENT;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, "Erro de validação nos dados enviados.");
        problemDetail.setInstance(URI.create(request.getRequestURI()));

        problemDetail.setTitle("Erro de Validação");
        problemDetail.setProperties(Map.of(
                "código", "VAL-001",
                "timestamp", Instant.now()
        ));

        ValidationErroDTO erros = new ValidationErroDTO(problemDetail);
        e.getBindingResult()
                .getFieldErrors()
                .forEach(fe -> erros.adicionarCampo(new MensagemCampo(fe.getField(), fe.getDefaultMessage())));

        return ResponseEntity.status(status.value()).body(erros);
    }

}

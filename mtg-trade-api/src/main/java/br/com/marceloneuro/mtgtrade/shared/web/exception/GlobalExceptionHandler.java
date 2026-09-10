package br.com.marceloneuro.mtgtrade.shared.web.exception;

import br.com.marceloneuro.mtgtrade.shared.web.exception.dto.ErroDTO;
import br.com.marceloneuro.mtgtrade.shared.web.exception.dto.MensagemCampo;
import br.com.marceloneuro.mtgtrade.shared.web.exception.dto.ValidationErroDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErroDTO> handleEntityNotFoundException (EntityNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ErroDTO erro = new ErroDTO(e.getMessage(), request.getRequestURI(),
                status.value(), Instant.now());

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroDTO> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        ErroDTO erro = new ErroDTO("E-mail, ou senha incorretos", request.getRequestURI(),
                status.value(), Instant.now());

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroDTO> handleDataIntegrityViolationException(DataIntegrityViolationException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        ErroDTO erro = new ErroDTO("E-mail já registrado", request.getRequestURI(),
                status.value(), Instant.now());

        return ResponseEntity.status(status).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErroDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_CONTENT;

        ValidationErroDTO erros = new ValidationErroDTO("Erro de validação nos dados enviados.", request.getRequestURI(),
                status.value(), Instant.now());

        e.getBindingResult()
                .getFieldErrors()
                .forEach(fe -> erros.adicionarCampo(new MensagemCampo(fe.getField(), fe.getDefaultMessage())));

        return ResponseEntity.status(status.value()).body(erros);
    }

}

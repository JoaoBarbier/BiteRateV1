package com.progweb.biterate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice // Captura exceções globais jogadas por qualquer Controller
public class GlobalExceptionHandler {

    // Trata erro 404 (Recurso não encontrado)
    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleNaoEncontrado(NaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildBody(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    // Trata erro 409 (Conflito de dados/regras de negócio)
    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<Map<String, Object>> handleConflito(ConflitoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildBody(HttpStatus.CONFLICT, ex.getMessage()));
    }

    // Trata erro 403 (Usuário logado mas sem permissão para a ação)
    @ExceptionHandler(SemPermissaoException.class)
    public ResponseEntity<Map<String, Object>> handleSemPermissao(SemPermissaoException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildBody(HttpStatus.FORBIDDEN, ex.getMessage()));
    }

    // Trata qualquer outra exceção genérica de tempo de execução (Erro 400)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Erro interno";
        return ResponseEntity.badRequest().body(buildBody(HttpStatus.BAD_REQUEST, msg));
    }

    // Trata erros de validação do @Valid (ex: campos obrigatórios vazios)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        String erros = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(buildBody(HttpStatus.BAD_REQUEST, erros));
    }

    // Método auxiliar para padronizar o JSON de resposta dos erros
    private Map<String, Object> buildBody(HttpStatus status, String mensagem) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("erro", mensagem);
        return body;
    }
}

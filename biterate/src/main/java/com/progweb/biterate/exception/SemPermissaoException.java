package com.progweb.biterate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Retorna automaticamente HTTP 403 Forbidden para o cliente
@ResponseStatus(HttpStatus.FORBIDDEN)
public class SemPermissaoException extends RuntimeException {

    // Repassa a mensagem personalizada de erro para a classe pai
    public SemPermissaoException(String mensagem) {
        super(mensagem);
    }
}

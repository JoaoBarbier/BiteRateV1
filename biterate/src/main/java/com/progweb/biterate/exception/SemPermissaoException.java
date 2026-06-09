package com.progweb.biterate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class SemPermissaoException extends RuntimeException {
    public SemPermissaoException(String mensagem) {
        super(mensagem);
    }
}

package com.progweb.biterate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//Define a condição de resposta do Spring Boot quando essa exceção for lançada respondendo o HTTP
//409 de maneira automática para o cliente.
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflitoException extends RuntimeException {

    // Construtor que recebe uma mensagem personalizada de erro e a repassa para a classe pai (RuntimeException).
    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}

package com.progweb.biterate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Exceção lançada quando um recurso não é encontrado
// @ResponseStatus garante que a resposta HTTP será 404 automaticamente
@ResponseStatus(HttpStatus.NOT_FOUND)
public class NaoEncontradoException extends RuntimeException {

    // Recebe a mensagem de erro e passa pro RuntimeException
    public NaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
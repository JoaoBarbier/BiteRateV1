package com.progweb.biterate.dto.response;

import lombok.Getter;

// Resposta do login
@Getter
public class LoginResponse {

    private final String token;
    private final ClienteResponse cliente;

    public LoginResponse(String token, ClienteResponse cliente) {
        this.token = token;
        this.cliente = cliente;
    }
}

package com.progweb.biterate.controller;

import com.progweb.biterate.dto.request.CadastroRequest;
import com.progweb.biterate.dto.request.LoginRequest;
import com.progweb.biterate.dto.response.ClienteResponse;
import com.progweb.biterate.dto.response.LoginResponse;
import com.progweb.biterate.service.Authservice;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor // Injeta o Authservice automaticamente
public class Authcontroller {

    private final Authservice authService;

    // Endpoint para criar conta nova (Retorna HTTP 201)
    @PostMapping("/cadastro")
    public ResponseEntity<ClienteResponse> cadastrar(@Valid @RequestBody CadastroRequest request) {
        ClienteResponse response = authService.cadastrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint para logar e receber o token (Retorna HTTP 200)
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}


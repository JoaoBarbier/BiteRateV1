package com.progweb.biterate.controller;

import com.progweb.biterate.dto.request.EditarPerfilRequest;
import com.progweb.biterate.dto.response.AvaliacaoResponse;
import com.progweb.biterate.dto.response.ClienteResponse;
import com.progweb.biterate.dto.response.RestauranteResumoResponse;
import com.progweb.biterate.model.Cliente;
import com.progweb.biterate.service.Clienteservice;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class Clientecontroller {

    private final Clienteservice clienteService;

    @GetMapping("/perfil")
    public ResponseEntity<ClienteResponse> buscarPerfil(
            @AuthenticationPrincipal Cliente clienteAutenticado) {
        return ResponseEntity.ok(clienteService.buscarPerfil(clienteAutenticado));
    }

    @PutMapping("/perfil")
    public ResponseEntity<ClienteResponse> editarPerfil(
            @AuthenticationPrincipal Cliente clienteAutenticado,
            @Valid @RequestBody EditarPerfilRequest request) {
        return ResponseEntity.ok(clienteService.editarPerfil(clienteAutenticado, request));
    }

    @GetMapping("/avaliacoes")
    public ResponseEntity<List<AvaliacaoResponse>> listarAvaliacoes(
            @AuthenticationPrincipal Cliente clienteAutenticado) {
        return ResponseEntity.ok(clienteService.listarAvaliacoes(clienteAutenticado));
    }

    @GetMapping("/favoritos")
    public ResponseEntity<List<RestauranteResumoResponse>> listarFavoritos(
            @AuthenticationPrincipal Cliente clienteAutenticado) {
        return ResponseEntity.ok(clienteService.listarFavoritos(clienteAutenticado));
    }
}

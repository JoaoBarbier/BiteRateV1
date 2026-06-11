package com.progweb.biterate.controller;

import com.progweb.biterate.dto.request.AvaliacaoRequest;
import com.progweb.biterate.dto.response.AvaliacaoResponse;
import com.progweb.biterate.model.Cliente;
import com.progweb.biterate.service.Avaliacaoservice;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/avaliacoes")
@RequiredArgsConstructor
public class Avaliacaocontroller {

    private final Avaliacaoservice avaliacaoService;

    // Lista as últimas avaliações (padrão: 6 itens)
    @GetMapping("/recentes")
    public ResponseEntity<Page<AvaliacaoResponse>> recentes(
            @RequestParam(defaultValue = "6") int size) {
        return ResponseEntity.ok(avaliacaoService.listarRecentes(size));
    }

    // Cria uma avaliação vinculada ao usuário logado
    @PostMapping
    public ResponseEntity<AvaliacaoResponse> criar(
            @Valid @RequestBody AvaliacaoRequest request,
            @AuthenticationPrincipal Cliente clienteAutenticado) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(avaliacaoService.criar(request, clienteAutenticado));
    }

    // Edita uma avaliação por ID (só o dono pode alterar)
    @PutMapping("/{id}")
    public ResponseEntity<AvaliacaoResponse> editar(
            @PathVariable Long id,
            @Valid @RequestBody AvaliacaoRequest request,
            @AuthenticationPrincipal Cliente clienteAutenticado) {
        return ResponseEntity.ok(avaliacaoService.editar(id, request, clienteAutenticado));
    }

    // Deleta uma avaliação por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id,
            @AuthenticationPrincipal Cliente clienteAutenticado) {
        avaliacaoService.excluir(id, clienteAutenticado);
        return ResponseEntity.noContent().build();
    }
}

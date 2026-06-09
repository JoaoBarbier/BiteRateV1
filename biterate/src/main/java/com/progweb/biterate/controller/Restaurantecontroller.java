package com.progweb.biterate.controller;

import com.progweb.biterate.dto.request.BuscarRestauranteRequest;
import com.progweb.biterate.dto.request.RestauranteRequest;
import com.progweb.biterate.dto.response.AvaliacaoResponse;
import com.progweb.biterate.dto.response.RestauranteResumoResponse;
import com.progweb.biterate.dto.response.RestauranteResponse;
import com.progweb.biterate.model.Cliente;
import com.progweb.biterate.service.Buscarservice;
import com.progweb.biterate.service.Restauranteservice;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurantes")
@RequiredArgsConstructor
public class Restaurantecontroller {

    private final Restauranteservice restauranteService;
    private final Buscarservice buscarService;

    @GetMapping("/buscar")
    public ResponseEntity<Page<RestauranteResumoResponse>> buscar(
            @RequestParam(required = false) String termo,
            @RequestParam(required = false) List<String> categorias,
            @RequestParam(required = false) Double notaMinima,
            @RequestParam(required = false) List<String> faixasPreco,
            @RequestParam(required = false) List<String> comodidades,
            @RequestParam(defaultValue = "false") boolean apenasAbertos,
            @RequestParam(defaultValue = "relevancia") String ordenacao,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        BuscarRestauranteRequest request = new BuscarRestauranteRequest();
        request.setTermo(termo);
        request.setCategorias(categorias);
        request.setNotaMinima(notaMinima);
        request.setFaixasPreco(faixasPreco);
        request.setComodidades(comodidades);
        request.setApenasAbertos(apenasAbertos);
        request.setOrdenacao(ordenacao);
        request.setPage(page);
        request.setSize(size);

        return ResponseEntity.ok(buscarService.buscarComComodidades(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestauranteResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(restauranteService.buscarPorId(id));
    }

    @GetMapping("/{id}/avaliacoes")
    public ResponseEntity<Page<AvaliacaoResponse>> listarAvaliacoes(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page) {

        return ResponseEntity.ok(restauranteService.listarAvaliacoes(id, page));
    }

    @PostMapping("/{id}/favoritar")
    public ResponseEntity<Void> favoritar(
            @PathVariable Long id,
            @AuthenticationPrincipal Cliente clienteAutenticado) {

        restauranteService.favoritar(id, clienteAutenticado);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/favoritado")
    public ResponseEntity<Boolean> isFavoritado(
            @PathVariable Long id,
            @AuthenticationPrincipal Cliente clienteAutenticado) {

        return ResponseEntity.ok(restauranteService.isFavoritado(id, clienteAutenticado));
    }

    @PostMapping
    public ResponseEntity<RestauranteResponse> cadastrar(
            @Valid @RequestBody RestauranteRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restauranteService.cadastrar(request));
    }
}

package com.progweb.biterate.dto.request;

import lombok.Data;

import java.util.List;

// Buscar e filtrar restaurantes
@Data
public class BuscarRestauranteRequest {

    private String termo;

    // Filtros opcionais
    private List<String> categorias;
    private Double notaMinima;
    private List<String> faixasPreco;
    private List<String> comodidades;

    private boolean apenasAbertos = false;

    private String ordenacao = "relevancia";

    // Paginação — primeira página com 12 resultados
    private int page = 0;
    private int size = 12;
}

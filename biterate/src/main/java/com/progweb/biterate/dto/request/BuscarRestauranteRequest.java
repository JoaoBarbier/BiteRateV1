package com.progweb.biterate.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class BuscarRestauranteRequest {

    private String termo;

    private List<String> categorias;

    private Double notaMinima;

    private List<String> faixasPreco;

    private List<String> comodidades;

    private boolean apenasAbertos = false;

    private String ordenacao = "relevancia";

    private int page = 0;
    private int size = 12;
}

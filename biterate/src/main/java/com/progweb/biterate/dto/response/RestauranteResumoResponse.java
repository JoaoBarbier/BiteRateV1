package com.progweb.biterate.dto.response;

import lombok.*;
 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestauranteResumoResponse {
 
    // usado nos cards do buscar.html
    private Long id;
    private String nome;
    private String categoria;
    private String faixaPreco;
    private String bairro;  
    private String cidade;
    private String fotoUrl;
    private Double mediaNote;
    private Integer totalAvaliacoes;
}
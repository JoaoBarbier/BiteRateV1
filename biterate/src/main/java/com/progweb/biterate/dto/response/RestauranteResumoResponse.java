package com.progweb.biterate.dto.response;

import lombok.*;

// Versão resumida do restaurante — usado nos cards da busca
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestauranteResumoResponse {

    private Long id;
    private String nome;
    private String categoria;
    private String faixaPreco;
    private String bairro;
    private String cidade;
    private String fotoUrl;

    // Avaliação resumida
    private Double mediaNote;
    private Integer totalAvaliacoes;
}

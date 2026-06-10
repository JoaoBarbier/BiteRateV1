package com.progweb.biterate.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// Dados completos de um restaurante — usado na página de detalhes
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestauranteResponse {

    private Long id;
    private String nome;
    private String categoria;
    private String descricao;
    private String faixaPreco;

    // Endereço
    private String cep;
    private String estado;
    private String rua;
    private String numero;
    private String bairro;
    private String cidade;
    private String complemento;

    private String telefone;
    private String comodidades;
    private String fotoUrl;

    // Estatísticas de avaliação
    private Double mediaNote;
    private Integer totalAvaliacoes;

    private LocalDateTime criadoEm;

    // Horários de funcionamento por dia
    private List<HorarioResponse> horarios;

    // Distribuição de notas
    private Map<Integer, Long> distribuicaoNotas;
}

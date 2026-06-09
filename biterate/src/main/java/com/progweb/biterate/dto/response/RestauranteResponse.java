package com.progweb.biterate.dto.response;

import lombok.*;
 
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
 
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
    private Double mediaNote;
    private Integer totalAvaliacoes;
    private LocalDateTime criadoEm;
    private List<HorarioResponse> horarios;
    private Map<Integer, Long> distribuicaoNotas;
}
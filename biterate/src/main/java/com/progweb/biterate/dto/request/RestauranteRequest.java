package com.progweb.biterate.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

// Cadastrar ou editar um restaurante
@Data
public class RestauranteRequest {

    @NotBlank(message = "Nome do restaurante é obrigatório")
    @Size(max = 100)
    private String nome;

    @NotBlank(message = "Categoria é obrigatória")
    private String categoria;

    @Size(max = 500)
    private String descricao;

    @Pattern(regexp = "^[1-4]$", message = "Faixa de preço inválida")
    private String faixaPreco;

    @NotBlank(message = "CEP é obrigatório")
    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido")
    private String cep;

    @NotBlank(message = "Estado é obrigatório")
    @Size(min = 2, max = 2)
    private String estado;

    @NotBlank(message = "Rua é obrigatória")
    private String rua;

    // Número
    @NotBlank(message = "Número é obrigatório")
    private String numero;

    @NotBlank(message = "Bairro é obrigatório")
    private String bairro;

    @NotBlank(message = "Cidade é obrigatória")
    private String cidade;

    private String complemento;

    @Size(max = 15)
    private String telefone;

    private String comodidades;

    private String fotoUrl;

    // Horários de funcionamento por dia da semana
    private List<HorarioRequest> horarios;
}

package com.progweb.biterate.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Dados de uma avaliação retornados ao frontend
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoResponse {

    private Long id;
    private Integer nota;

    // Notas por categoria
    private Integer notaComida;
    private Integer notaAtendimento;
    private Integer notaAmbiente;

    private String titulo;
    private String comentario;
    private LocalDate dataVisita;
    private String tipoVisita;
    private LocalDateTime criadoEm;

    // Dados do autor
    private Long clienteId;
    private String clienteNome;
    private String clienteUsername;
    private String clienteFotoUrl;

    // Dados do restaurante avaliado
    private Long restauranteId;
    private String restauranteNome;
}

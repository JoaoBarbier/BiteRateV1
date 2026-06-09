package com.progweb.biterate.dto.response;

import lombok.*;
 
import java.time.LocalDate;
import java.time.LocalDateTime;
 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoResponse {
 
    private Long id;
    private Integer nota;
    private Integer notaComida;
    private Integer notaAtendimento;
    private Integer notaAmbiente;
    private String titulo;
    private String comentario;
    private LocalDate dataVisita;
    private String tipoVisita;
    private LocalDateTime criadoEm;
 
    // dados do autor
    private Long clienteId;
    private String clienteNome;
    private String clienteUsername;
    private String clienteFotoUrl;
 
    // dados do restaurante
    private Long restauranteId;
    private String restauranteNome;
}
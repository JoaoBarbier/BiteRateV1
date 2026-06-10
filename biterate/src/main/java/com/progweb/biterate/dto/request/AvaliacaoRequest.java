package com.progweb.biterate.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

// Criar ou editar uma avaliação
@Data
public class AvaliacaoRequest {

    @NotNull(message = "ID do restaurante é obrigatório")
    private Long restauranteId;
 
    @NotNull(message = "Nota é obrigatória")
    @Min(value = 1, message = "Nota mínima é 1")
    @Max(value = 5, message = "Nota máxima é 5")
    private Integer nota;

    // Avaliações Opcionais
    @Min(1) @Max(5)
    private Integer notaComida;
    @Min(1) @Max(5)
    private Integer notaAtendimento;
    @Min(1) @Max(5)
    private Integer notaAmbiente;

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 100)
    private String titulo;

    @NotBlank(message = "Comentário é obrigatório")
    @Size(min = 30, max = 1000, message = "Comentário deve ter entre 30 e 1000 caracteres")
    private String comentario;

    private LocalDate dataVisita;

    @Pattern(regexp = "^(sozinho|casal|familia|amigos|negocios)$", message = "Tipo de visita inválido")
    private String tipoVisita;
}
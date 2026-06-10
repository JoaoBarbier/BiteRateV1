package com.progweb.biterate.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

// Horário de funcionamento de um dia da semana (usado dentro de RestauranteRequest)
@Data
public class HorarioRequest {

    @NotBlank(message = "Dia é obrigatório")
    @Pattern(regexp = "^(seg|ter|qua|qui|sex|sab|dom)$", message = "Dia inválido")
    private String dia;

    private boolean aberto;

    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Horário de abertura inválido")
    private String horaAbertura;

    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Horário de fechamento inválido")
    private String horaFechamento;
}

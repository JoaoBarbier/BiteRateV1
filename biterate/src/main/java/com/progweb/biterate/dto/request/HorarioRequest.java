package com.progweb.biterate.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
 
@Data
public class HorarioRequest {
 
    // seg, ter, qua, qui, sex, sab, dom
    @NotBlank(message = "Dia é obrigatório")
    @Pattern(regexp = "^(seg|ter|qua|qui|sex|sab|dom)$", message = "Dia inválido")
    private String dia;
 
    private boolean aberto;
 
    // formato HH:mm
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Horário de abertura inválido")
    private String horaAbertura;
 
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Horário de fechamento inválido")
    private String horaFechamento;
}
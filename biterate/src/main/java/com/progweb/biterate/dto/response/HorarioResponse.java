package com.progweb.biterate.dto.response;

import lombok.*;

// Horário de funcionamento de um dia (usado dentro de RestauranteResponse)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HorarioResponse {

    private String dia;
    private boolean aberto;
    private String horaAbertura;
    private String horaFechamento;
}

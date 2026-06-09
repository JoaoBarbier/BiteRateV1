package com.progweb.biterate.dto.response;

import lombok.*;
 
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
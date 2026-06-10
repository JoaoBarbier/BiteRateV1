package com.progweb.biterate.dto.response;

import lombok.*;

import java.time.LocalDateTime;

// Dados do cliente retornados ao frontend
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponse {

    private Long id;
    private String nome;
    private String sobrenome;
    private String username;
    private String email;
    private String bio;
    private String cidade;
    private String fotoUrl;

    // Estatísticas do perfil
    private int totalAvaliacoes;
    private int totalFavoritos;

    private LocalDateTime criadoEm;
}

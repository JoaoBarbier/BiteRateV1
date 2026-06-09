package com.progweb.biterate.model;

import jakarta.persistence.*;
import lombok.*;
 
@Entity
@Table(name = "horarios_funcionamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioFuncionamento {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    // seg, ter, qua, qui, sex, sab, dom
    @Column(nullable = false, length = 3)
    private String dia;
 
    @Column(nullable = false)
    private boolean aberto;
 
    // formato HH:mm — nulos quando aberto = false
    @Column(name = "hora_abertura", length = 5)
    private String horaAbertura;
 
    @Column(name = "hora_fechamento", length = 5)
    private String horaFechamento;
 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurante_id", nullable = false)
    private Restaurante restaurante;
}
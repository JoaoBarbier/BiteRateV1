package com.progweb.biterate.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "restaurantes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 80)
    private String categoria;

    @Column(length = 500)
    private String descricao;

    @Column(name = "faixa_preco", length = 1)
    private String faixaPreco;

    @Column(length = 9)
    private String cep;

    @Column(length = 2)
    private String estado;

    @Column(nullable = false, length = 150)
    private String rua;

    @Column(length = 20)
    private String numero;

    @Column(length = 80)
    private String bairro;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(length = 100)
    private String complemento;

    @Column(length = 15)
    private String telefone;

    @Column(length = 200)
    private String comodidades;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Builder.Default
    @Column(name = "media_nota", columnDefinition = "DECIMAL(3,2) DEFAULT 0")
    private Double mediaNote = 0.0;

    @Builder.Default
    @Column(name = "total_avaliacoes", columnDefinition = "INT DEFAULT 0")
    private Integer totalAvaliacoes = 0;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorarioFuncionamento> horarios;

    @OneToMany(mappedBy = "restaurante", cascade = CascadeType.ALL)
    private List<Avaliacao> avaliacoes;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }
}

package com.progweb.biterate.repository;

import com.progweb.biterate.model.Avaliacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

// Acesso ao banco para a entidade Avaliacao
@Repository
public interface Avaliacaorepository extends JpaRepository<Avaliacao, Long> {

    // JPA
    Page<Avaliacao> findByRestauranteId(Long restauranteId, Pageable pageable);
    List<Avaliacao> findByClienteIdOrderByCriadoEmDesc(Long clienteId);
    boolean existsByClienteIdAndRestauranteId(Long clienteId, Long restauranteId);
    long countByRestauranteId(Long restauranteId);
    long countByClienteId(Long clienteId);
    Page<Avaliacao> findAllByOrderByCriadoEmDesc(Pageable pageable);

    // Query manual para calcular a média das notas de um restaurante
    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.restaurante.id = :restauranteId")
    Double calcularMediaNota(@Param("restauranteId") Long restauranteId);

    // Query manual para contar avaliações agrupadas por nota
    @Query("SELECT a.nota AS nota, COUNT(a) AS contagem FROM Avaliacao a WHERE a.restaurante.id = :id GROUP BY a.nota")
    List<NotaContagem> contarPorNota(@Param("id") Long id);

    // Interface de projeção — mapeia o resultado da query acima sem precisar de uma classe extra
    interface NotaContagem {
        Integer getNota();
        Long getContagem();
    }
}
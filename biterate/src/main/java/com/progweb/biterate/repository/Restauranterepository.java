package com.progweb.biterate.repository;

import com.progweb.biterate.model.Restaurante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Restauranterepository extends JpaRepository<Restaurante, Long> {

    @Query("SELECT r FROM Restaurante r WHERE " +
           "(:termo IS NULL OR LOWER(r.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(r.cidade) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(r.bairro) LIKE LOWER(CONCAT('%', :termo, '%'))) AND " +
           "(:#{#categorias == null || #categorias.isEmpty()} = true OR r.categoria IN :categorias) AND " +
           "(:notaMinima IS NULL OR r.mediaNote >= :notaMinima) AND " +
           "(:#{#faixasPreco == null || #faixasPreco.isEmpty()} = true OR r.faixaPreco IN :faixasPreco) AND " +
           "(:c1 IS NULL OR r.comodidades LIKE CONCAT('%', :c1, '%')) AND " +
           "(:c2 IS NULL OR r.comodidades LIKE CONCAT('%', :c2, '%')) AND " +
           "(:c3 IS NULL OR r.comodidades LIKE CONCAT('%', :c3, '%')) AND " +
           "(:c4 IS NULL OR r.comodidades LIKE CONCAT('%', :c4, '%')) AND " +
           "(:c5 IS NULL OR r.comodidades LIKE CONCAT('%', :c5, '%')) AND " +
           "(:apenasAbertos = false OR EXISTS (" +
           "  SELECT h FROM HorarioFuncionamento h " +
           "  WHERE h.restaurante = r " +
           "  AND h.dia = :diaAtual " +
           "  AND h.aberto = true " +
           "  AND h.horaAbertura <= :horaAtual " +
           "  AND h.horaFechamento > :horaAtual" +
           "))")
    Page<Restaurante> buscarComFiltros(
            @Param("termo") String termo,
            @Param("categorias") List<String> categorias,
            @Param("notaMinima") Double notaMinima,
            @Param("faixasPreco") List<String> faixasPreco,
            @Param("c1") String c1,
            @Param("c2") String c2,
            @Param("c3") String c3,
            @Param("c4") String c4,
            @Param("c5") String c5,
            @Param("apenasAbertos") boolean apenasAbertos,
            @Param("diaAtual") String diaAtual,
            @Param("horaAtual") String horaAtual,
            Pageable pageable);

    boolean existsByRuaAndNumeroAndCidade(String rua, String numero, String cidade);
}

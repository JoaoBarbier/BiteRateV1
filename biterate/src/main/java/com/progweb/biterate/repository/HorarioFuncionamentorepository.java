package com.progweb.biterate.repository;

import com.progweb.biterate.model.HorarioFuncionamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Acesso ao banco para a entidade HorarioFuncionamento
@Repository
public interface HorarioFuncionamentorepository extends JpaRepository<HorarioFuncionamento, Long> {

    // JPA
    List<HorarioFuncionamento> findByRestauranteId(Long restauranteId);
}

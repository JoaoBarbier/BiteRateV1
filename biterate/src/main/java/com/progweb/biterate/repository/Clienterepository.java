package com.progweb.biterate.repository;

import com.progweb.biterate.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Clienterepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("SELECT COUNT(r) FROM Cliente c JOIN c.favoritos r WHERE c.id = :clienteId")
    long countFavoritosByClienteId(@Param("clienteId") Long clienteId);
}

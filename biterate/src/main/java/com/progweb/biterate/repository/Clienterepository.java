package com.progweb.biterate.repository;

import com.progweb.biterate.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Acesso ao banco para a entidade Cliente
@Repository
public interface Clienterepository extends JpaRepository<Cliente, Long> {

    // JPA
    Optional<Cliente> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // Query manual para contar favoritos do cliente via relacionamento @ManyToMany
    @Query("SELECT COUNT(r) FROM Cliente c JOIN c.favoritos r WHERE c.id = :clienteId")
    long countFavoritosByClienteId(@Param("clienteId") Long clienteId);
}
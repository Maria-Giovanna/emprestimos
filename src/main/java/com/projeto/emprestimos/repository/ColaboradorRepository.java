package com.projeto.emprestimos.repository;

import com.projeto.emprestimos.model.Colaborador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {
    boolean existsByEmail(String email);
    Optional<Colaborador> findByEmail(String email);
}

package com.projeto.emprestimos.repository;

import com.projeto.emprestimos.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    // esse equipamento já teve empréstimo?
    boolean existsByEquipamentoId(Long equipamentoId);

    // histórico da pessoa
    List<Emprestimo> findByColaboradorId(Long colaboradorId);

    //os atrasados
    List<Emprestimo> findByDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(LocalDate data);

    //pessoa tem algum empréstimo atrasado?
    boolean existsByColaboradorIdAndDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(Long colaboradorId, LocalDate data);

    //essa pessoa já tem algum empréstimo em aberto (ainda não devolvido)?
    boolean existsByColaboradorIdAndDataDevolucaoIsNull(Long colaboradorId);
}

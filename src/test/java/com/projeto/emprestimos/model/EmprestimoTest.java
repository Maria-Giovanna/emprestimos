package com.projeto.emprestimos.model;
import com.projeto.emprestimos.exception.ConflitoException;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class EmprestimoTest {

    @Test
    void criadoEm14DeNovembroTemPrazoEm28DeNovembro(){
        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), LocalDate.of(2026, 11, 14));

        assertEquals(LocalDate.of(2026, 11, 14), emprestimo.getDataEmprestimo());
        assertEquals(LocalDate.of(2026, 11, 28), emprestimo.getDataPrevistaDevolucao());
    }

    @Test
    void devolverEmprestimoAbertoGuardaADataDaDevolucao(){
        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), LocalDate.of(2026, 11, 14));

        emprestimo.devolver(LocalDate.of(2026, 11, 20));

        assertEquals(LocalDate.of(2026, 11, 20), emprestimo.getDataDevolucao());
    }

    @Test
    void naoDevolveOMesmoEmprestimoDuasVezes(){

        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), LocalDate.of(2026, 11, 14));
        emprestimo.devolver(LocalDate.of(2026, 11, 20));

        assertThrows(ConflitoException.class, () -> emprestimo.devolver(LocalDate.of(2026, 11, 21)));
    }

    @Test
    void emprestimoVencidoENaoDevolvidoEstaAtrasado(){
    //Criado em 27/10 e prazo 10/11

        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), LocalDate.of(2026, 10, 27));

        assertTrue(emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14)));
    }

    @Test
    void emprestimoDentroDoPrazoNaoEstaAtrasado(){
    //Criado em 06/11 e prazo 20/11

        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), LocalDate.of(2026, 11, 6));

        assertFalse(emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14)));
    }

    @Test
    void emprestimoDevolvidoNaoEstaAtrasado(){
    //Criado em 27/10 e prazo 10/11, mas devolvido em 12/11

        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), LocalDate.of(2026, 10, 27));
        emprestimo.devolver(LocalDate.of(2026, 11, 12));

        assertFalse(emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14)));
    }

    @Test
    void noDiaDoPrazoAindaNaoEstaAtrasado(){
    //Criado em 31/10 e o prazo 14/11

        Emprestimo emprestimo = new Emprestimo(new Equipamento(), new Colaborador(), LocalDate.of(2026, 10, 31));

        assertFalse(emprestimo.estaAtrasadoEm(LocalDate.of(2026, 11, 14)));
    }
}

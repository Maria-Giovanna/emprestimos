package com.projeto.emprestimos.model;

import com.projeto.emprestimos.exception.ConflitoException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EquipamentoTest {

    @Test
    void emprestarEquipamentoDisponivelDeixaIndisponivel(){
        Equipamento equipamento = new Equipamento();

        equipamento.emprestar();

        assertFalse(equipamento.isDisponivel());
    }

    @Test
    void naoEmprestaEquipamentoIndisponivel(){
        Equipamento equipamento = new Equipamento();

        equipamento.emprestar();

        assertThrows(ConflitoException.class, () -> equipamento.emprestar());
    }

    @Test
    void devolverDeixaEquipamentoDisponivel(){
        Equipamento equipamento = new Equipamento();
        equipamento.emprestar();

        equipamento.devolver();

        assertTrue(equipamento.isDisponivel());
    }

}

package com.projeto.emprestimos.dto;

import jakarta.validation.constraints.NotNull;

public record EmprestimoRequest(
        @NotNull(message = "O equipamento é obrigatório")
        Long equipamentoId) {}

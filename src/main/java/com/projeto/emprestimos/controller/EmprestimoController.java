package com.projeto.emprestimos.controller;

import com.projeto.emprestimos.dto.EmprestimoRequest;
import com.projeto.emprestimos.model.Emprestimo;
import com.projeto.emprestimos.service.EmprestimoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Emprestimo emprestar(
            @RequestBody
            @Valid
            EmprestimoRequest pedido,

            Authentication autenticacao
    ) {
        return emprestimoService.emprestar(pedido.equipamentoId(), autenticacao.getName());
    }

    @GetMapping
    public List<Emprestimo> listar(
            @RequestParam(required = false)
            Boolean atrasados
    ) {
        return emprestimoService.listar(atrasados);
    }

    @GetMapping("/meus")
    public List<Emprestimo> meus(Authentication autenticacao) {
        return emprestimoService.listarDoEmail(autenticacao.getName());
    }

    @GetMapping("/{id}")
    public Emprestimo buscar(@PathVariable Long id) {
        return emprestimoService.buscar(id);
    }

    @PutMapping("/{id}/devolucao")
    public Emprestimo devolver(
            @PathVariable Long id,

            Authentication autenticacao) {
        // algum dos perfis de quem está logado é ROLE_ADMIN?
        boolean ehAdmin = autenticacao.getAuthorities()
                .stream()
                .anyMatch(perfil -> perfil.getAuthority().equals("ROLE_ADMIN"));

        return emprestimoService.devolver(id, autenticacao.getName(), ehAdmin);
    }
}
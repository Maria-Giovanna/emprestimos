package com.projeto.emprestimos.controller;

import com.projeto.emprestimos.dto.EmprestimoRequest;
import com.projeto.emprestimos.model.Emprestimo;
import com.projeto.emprestimos.service.EmprestimoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {

    //ATRIBUTOS
    private EmprestimoService emprestimoService;

    //CONSTRUTOR
    public EmprestimoController(EmprestimoService emprestimoService){
        this.emprestimoService = emprestimoService;
    }

    //METODOS
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Emprestimo emprestar(@RequestBody @Valid EmprestimoRequest pedido){
        return emprestimoService.emprestar(pedido);
    }

    @GetMapping
    public List<Emprestimo> listar(@RequestParam (required = false) Boolean atrasados){
        return emprestimoService.listar(atrasados);
    }

    @PutMapping("/{id}")
    public Emprestimo buscar(@PathVariable Long id){
        return emprestimoService.buscar(id);
    }

    @PutMapping("/{id}/devolucao")
    public Emprestimo devolver(@PathVariable Long id){
        return emprestimoService.devolver(id);
    }


}

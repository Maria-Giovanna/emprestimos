package com.projeto.emprestimos.controller;

import com.projeto.emprestimos.model.Colaborador;
import com.projeto.emprestimos.model.Emprestimo;
import com.projeto.emprestimos.service.ColaboradorService;
import com.projeto.emprestimos.service.EmprestimoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/colaboradores")
public class ColaboradorController {

    //ATRIBUTOS
    private ColaboradorService colaboradorService;
    private EmprestimoService emprestimoService;

    //CONSTRUTOR
    public ColaboradorController(ColaboradorService colaboradorService, EmprestimoService emprestimoService){
        this.colaboradorService = colaboradorService;
        this.emprestimoService = emprestimoService;
    }

    //METODOS
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Colaborador cadastrar(@RequestBody Colaborador colaborador){
        return colaboradorService.cadastrar(colaborador);
    }

    @GetMapping
    public List<Colaborador> listar(){
        return colaboradorService.listar();
    }

    @GetMapping("/{id}")
    public Colaborador buscar(@PathVariable Long id){
        return colaboradorService.buscar(id);
    }

    @GetMapping("/{id}/emprestimos")
    public List<Emprestimo> emprestimos(@PathVariable Long id){
        return emprestimoService.listarDoColaborador(id);
    }
}

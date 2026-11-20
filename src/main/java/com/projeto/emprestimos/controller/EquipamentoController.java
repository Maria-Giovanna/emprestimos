package com.projeto.emprestimos.controller;

import com.projeto.emprestimos.model.Equipamento;
import com.projeto.emprestimos.model.TipoEquipamento;
import com.projeto.emprestimos.service.EquipamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/equipamentos")
public class EquipamentoController {

    private EquipamentoService equipamentoService;

    public EquipamentoController(EquipamentoService equipamentoService){
        this.equipamentoService = equipamentoService;
    }

    @GetMapping
    public List<Equipamento> listar(
            @RequestParam(required = false)TipoEquipamento tipo,
            @RequestParam(required = false)Boolean disponivel)
    {return equipamentoService.listar(tipo,disponivel);}

    @GetMapping("/{id}")
    public Equipamento buscar(@PathVariable Long id){
        return equipamentoService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Equipamento cadastrar(@RequestBody @Valid Equipamento equipamento){
        return equipamentoService.cadastrar(equipamento);
    }

}

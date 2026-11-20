package com.projeto.emprestimos.service;


import com.projeto.emprestimos.exception.ConflitoException;
import com.projeto.emprestimos.exception.NaoEncontradoException;
import com.projeto.emprestimos.model.Equipamento;
import com.projeto.emprestimos.model.TipoEquipamento;
import com.projeto.emprestimos.repository.EquipamentoRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/equipamentos")
public class EquipamentoService {

    //ATRIBUTOS
    private EquipamentoRepository equipamentoRepository;

    //CONSTRUTOR
    public EquipamentoService(EquipamentoRepository equipamentoRepository){
        this.equipamentoRepository=equipamentoRepository;
    }

    //POSSIVEIS ROTAS
    public List<Equipamento> listar (TipoEquipamento tipo, Boolean disponivel){
        if (tipo!=null && disponivel !=null){
            return equipamentoRepository.findByTipoAndDisponivel(tipo, disponivel);
        } else if (tipo!=null) {
            return equipamentoRepository.findByTipo(tipo);
        } else if (disponivel != null) {
            return equipamentoRepository.findByDisponivel(disponivel);
        }else {
            return equipamentoRepository.findAll();
        }
    }

    public Equipamento buscar(Long id){
        return equipamentoRepository.findById(id)
                .orElseThrow( () -> new NaoEncontradoException("Equipamento não encontrado!"));
    }

    public Equipamento cadastrar(Equipamento equipamento){
        if (equipamentoRepository.existsByPatrimonio(equipamento.getPatrimonio())){
            throw new ConflitoException("O patrimônio já foi cadastrado.");
        }
        else{
            equipamento.setDisponivel(true);
            return equipamentoRepository.save(equipamento);
        }
    }



}

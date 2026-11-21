package com.projeto.emprestimos.service;


import com.projeto.emprestimos.exception.ConflitoException;
import com.projeto.emprestimos.exception.NaoEncontradoException;
import com.projeto.emprestimos.model.Equipamento;
import com.projeto.emprestimos.model.TipoEquipamento;
import com.projeto.emprestimos.repository.EmprestimoRepository;
import com.projeto.emprestimos.repository.EquipamentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipamentoService {

    //ATRIBUTOS
    private final EquipamentoRepository equipamentoRepository;
    private final EmprestimoRepository emprestimoRepository;

    //CONSTRUTOR
    public EquipamentoService(EquipamentoRepository equipamentoRepository, EmprestimoRepository emprestimoRepository){
        this.equipamentoRepository=equipamentoRepository;
        this.emprestimoRepository = emprestimoRepository;
    }

    //METODOS
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
                .orElseThrow( () -> new NaoEncontradoException("O Equipamento não foi encontrado!"));
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

    public Equipamento atualizar(Long id, Equipamento dados){
        Equipamento equipamento = buscar(id);
        equipamento.setNome(dados.getNome());
        equipamento.setTipo(dados.getTipo());
        return equipamentoRepository.save(equipamento);
    }

    public void remover(Long id){
        Equipamento equipamento = buscar(id);
        if (emprestimoRepository.existsByEquipamentoId(id)){
            throw new ConflitoException("Equipamento já tem um empréstimo registrado.");
        }
        equipamentoRepository.delete(equipamento);
    }


}

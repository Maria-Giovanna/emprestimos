package com.projeto.emprestimos.service;

import com.projeto.emprestimos.dto.EmprestimoRequest;
import com.projeto.emprestimos.exception.ConflitoException;
import com.projeto.emprestimos.exception.NaoEncontradoException;
import com.projeto.emprestimos.model.Colaborador;
import com.projeto.emprestimos.model.Emprestimo;
import com.projeto.emprestimos.model.Equipamento;
import com.projeto.emprestimos.repository.EmprestimoRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmprestimoService {

   //ATRIBUTOS
    private final EmprestimoRepository emprestimoRepository;
    private final EquipamentoService equipamentoService;
    private final ColaboradorService colaboradorService;

    //CONSTRUTOR
    public EmprestimoService(EmprestimoRepository emprestimoRepository, EquipamentoService equipamentoService, ColaboradorService colaboradorService){
        this.equipamentoService = equipamentoService;
        this.emprestimoRepository = emprestimoRepository;
        this.colaboradorService = colaboradorService;
    }

    //METODOS
    public Emprestimo buscar(Long id){
        return emprestimoRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Empréstimo não encontrado!"));
    }

    public List<Emprestimo> listarDoColaborador(Long colaboradorId){
        colaboradorService.buscar(colaboradorId);
        return emprestimoRepository.findByColaboradorId(colaboradorId);
    }

    @Transactional
    public Emprestimo emprestar(EmprestimoRequest pedido){
        Equipamento equipamento = equipamentoService.buscar(pedido.equipamentoId());
        Colaborador colaborador = colaboradorService.buscar(pedido.colaboradorId());

        if (emprestimoRepository.existsByColaboradorIdAndDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(colaborador.getId(), LocalDate.now())){
            throw new ConflitoException("Esse Colaborador tem empréstimo atrasado.");
        }
        equipamento.emprestar();

        Emprestimo emprestimo = new Emprestimo(equipamento, colaborador, LocalDate.now());
        return emprestimoRepository.save(emprestimo);
    }

    @Transactional
    public Emprestimo devolver(Long id){
        Emprestimo emprestimo = buscar(id);
        emprestimo.devolver(LocalDate.now());
        emprestimo.getEquipamento().devolver();
        return emprestimoRepository.save(emprestimo);
    }

    public List<Emprestimo> listar(Boolean atrasado){
        if (Boolean.TRUE.equals(atrasado)){
            return emprestimoRepository.findByDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(LocalDate.now());
        }
        return emprestimoRepository.findAll();
    }


}

package com.projeto.emprestimos.service;

import com.projeto.emprestimos.exception.ConflitoException;
import com.projeto.emprestimos.exception.NaoEncontradoException;
import com.projeto.emprestimos.model.Colaborador;
import com.projeto.emprestimos.model.Emprestimo;
import com.projeto.emprestimos.model.Equipamento;
import com.projeto.emprestimos.repository.EmprestimoRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmprestimoService {

    //ATRIBUTOS
    private final EmprestimoRepository emprestimoRepository;
    private final EquipamentoService equipamentoService;
    private final ColaboradorService colaboradorService;


    //CONSTRUTOR
    public EmprestimoService(
            EmprestimoRepository emprestimoRepository,
            EquipamentoService equipamentoService,
            ColaboradorService colaboradorService
    ) {
        this.emprestimoRepository = emprestimoRepository;
        this.equipamentoService = equipamentoService;
        this.colaboradorService = colaboradorService;
    }


    //METODOS DE CONSULTA
    public List<Emprestimo> listar(Boolean atrasados) {
        if (Boolean.TRUE.equals(atrasados)) {
            return emprestimoRepository.findByDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(LocalDate.now());
        }
        return emprestimoRepository.findAll();
    }

    public List<Emprestimo> listarDoColaborador(Long colaboradorId) {
        colaboradorService.buscar(colaboradorId);   // 404 se a pessoa não existir
        return emprestimoRepository.findByColaboradorId(colaboradorId);
    }

    public List<Emprestimo> listarDoEmail(String email) {
        Colaborador colaborador = colaboradorService.buscarPorEmail(email);
        return emprestimoRepository.findByColaboradorId(colaborador.getId());
    }

    public Emprestimo buscar(Long id) {
        return emprestimoRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Empréstimo não encontrado!"));
    }


    //METODOS DE AÇÃO
    @Transactional
    public Emprestimo emprestar(Long equipamentoId, String emailLogado) {

        Equipamento equipamento = equipamentoService.buscar(equipamentoId);
        Colaborador colaborador = colaboradorService.buscarPorEmail(emailLogado);
        LocalDate hoje = LocalDate.now();

        //quem está atrasado não pega outro
        boolean temAtrasado = emprestimoRepository.existsByColaboradorIdAndDataDevolucaoIsNullAndDataPrevistaDevolucaoBefore(
                colaborador.getId(),
                hoje
        );
        if (temAtrasado) {
            throw new ConflitoException("Colaborador tem empréstimo atrasado");
        }

        // R11: um equipamento por vez
        boolean temEmAberto = emprestimoRepository.existsByColaboradorIdAndDataDevolucaoIsNull(colaborador.getId());
        if (temEmAberto) {
            throw new ConflitoException("Você já tem um equipamento emprestado. Devolva antes de pegar outro");
        }

        equipamento.emprestar();
        Emprestimo emprestimo = new Emprestimo(equipamento, colaborador, hoje);
        return emprestimoRepository.save(emprestimo);
    }

    @Transactional
    public Emprestimo devolver(Long id, String emailLogado, boolean ehAdmin) {
        Emprestimo emprestimo = buscar(id);

        // controle de acesso: só quem pegou (ou o ADMIN) devolve
        String emailDoDono = emprestimo.getColaborador().getEmail();
        boolean ehDono = emailDoDono.equals(emailLogado);
        if (!ehAdmin && !ehDono) {
            throw new AccessDeniedException("Esse empréstimo é de outra pessoa");
        }

        emprestimo.devolver(LocalDate.now());
        emprestimo.getEquipamento().devolver();
        return emprestimoRepository.save(emprestimo);
    }
}
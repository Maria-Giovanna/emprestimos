package com.projeto.emprestimos.service;

import com.projeto.emprestimos.exception.ConflitoException;
import com.projeto.emprestimos.exception.NaoEncontradoException;
import com.projeto.emprestimos.model.Colaborador;
import com.projeto.emprestimos.repository.ColaboradorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColaboradorService {

    //ATRIBUTO
    private final ColaboradorRepository colaboradorRepository;

    //CONSTRUTOR
    public ColaboradorService(ColaboradorRepository colaboradorRepository){
        this.colaboradorRepository = colaboradorRepository;
    }

    //METODOS
    public List<Colaborador> listar(){
        return colaboradorRepository.findAll();
    }

    public Colaborador buscar(Long id){
        return colaboradorRepository.findById(id).orElseThrow( ()-> new NaoEncontradoException("Colaborador não encontrado"));
    }

    public Colaborador cadastrar(Colaborador colaborador){
        if (colaboradorRepository.existsByEmail(colaborador.getEmail())){
            throw new ConflitoException("E-mail já cadastrado, por favor, tente novamente com um outro e-mail.");
        }else {
            colaborador.setId(null);
            return colaboradorRepository.save(colaborador);
        }
    }
}

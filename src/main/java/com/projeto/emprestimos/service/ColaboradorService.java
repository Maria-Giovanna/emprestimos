package com.projeto.emprestimos.service;

import com.projeto.emprestimos.dto.ColaboradorRequest;
import com.projeto.emprestimos.exception.ConflitoException;
import com.projeto.emprestimos.exception.NaoEncontradoException;
import com.projeto.emprestimos.model.Colaborador;
import com.projeto.emprestimos.model.Perfil;
import com.projeto.emprestimos.repository.ColaboradorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColaboradorService {

    //ATRIBUTO
    private final ColaboradorRepository colaboradorRepository;
    private final PasswordEncoder passwordEncoder;

    //CONSTRUTOR
    public ColaboradorService(ColaboradorRepository colaboradorRepository, PasswordEncoder passwordEncoder){
        this.colaboradorRepository = colaboradorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //METODOS
    public List<Colaborador> listar(){
        return colaboradorRepository.findAll();
    }

    public Colaborador buscar(Long id){
        return colaboradorRepository.findById(id).orElseThrow( ()-> new NaoEncontradoException("Colaborador não encontrado"));
    }

    public Colaborador buscarPorEmail(String email){
        return colaboradorRepository.findByEmail(email)
                .orElseThrow(() -> new NaoEncontradoException("Colaborador não encontrado"));
    }

    public Colaborador cadastrar(ColaboradorRequest pedido){
        // R3: e-mail único
        if (colaboradorRepository.existsByEmail(pedido.email())) {
            throw new ConflitoException("E-mail já cadastrado");
        }

        // se o admin não escolheu o perfil, a pessoa entra como COLABORADOR
        Perfil perfil = pedido.perfil();
        if (perfil == null) {
            perfil = Perfil.COLABORADOR;
        }

        Colaborador colaborador = new Colaborador();
        colaborador.setNome(pedido.nome());
        colaborador.setEmail(pedido.email());
        colaborador.setCargo(pedido.cargo());
        colaborador.setSenha(passwordEncoder.encode(pedido.senha()));
        colaborador.setPerfil(perfil);

        return colaboradorRepository.save(colaborador);
    }
}

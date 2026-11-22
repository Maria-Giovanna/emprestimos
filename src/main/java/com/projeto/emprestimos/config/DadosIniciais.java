package com.projeto.emprestimos.config;

import com.projeto.emprestimos.model.Colaborador;
import com.projeto.emprestimos.model.Equipamento;
import com.projeto.emprestimos.model.Perfil;
import com.projeto.emprestimos.model.TipoEquipamento;
import com.projeto.emprestimos.repository.ColaboradorRepository;
import com.projeto.emprestimos.repository.EquipamentoRepository;
import jakarta.websocket.OnClose;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DadosIniciais implements CommandLineRunner {

    //ATRIBUTOS
    private final ColaboradorRepository colaboradorRepository;
    private final EquipamentoRepository equipamentoRepository;
    private final PasswordEncoder passwordEncoder;
    private final String senhaAdmin;
    private final String senhaDemo;

    //CONSTRUTOR
    public DadosIniciais(
            ColaboradorRepository colaboradorRepository,
            EquipamentoRepository equipamentoRepository,
            PasswordEncoder passwordEncoder,
            @Value("${acervo.admin.senha}") String senhaAdmin,
            @Value("${acervo.demo.senha}") String senhaDemo) {

        this.colaboradorRepository = colaboradorRepository;
        this.equipamentoRepository = equipamentoRepository;
        this.passwordEncoder = passwordEncoder;
        this.senhaAdmin = senhaAdmin;
        this.senhaDemo = senhaDemo;
    }

    @Override
    public void run(String... args) {
        cadastrarPessoa("Administração Acervo", "admin@acervo.com", "TI", senhaAdmin, Perfil.ADMIN);
        cadastrarPessoa("Ana Souza", "ana@acervo.com", "Analista de Marketing", senhaDemo, Perfil.COLABORADOR);

        if (equipamentoRepository.count() == 0) {
            cadastrarModelo("NB", 1, 3, "Notebook Dell Latitude 5450", TipoEquipamento.NOTEBOOK);
            cadastrarModelo("NB", 4, 2, "MacBook Air 13 M3", TipoEquipamento.NOTEBOOK);
            cadastrarModelo("MN", 1, 3, "Monitor Dell P2425H", TipoEquipamento.MONITOR);
            cadastrarModelo("HS", 1, 2, "Headset Jabra Evolve2 55", TipoEquipamento.HEADSET);
            cadastrarModelo("TC", 1, 2, "Teclado Logitech MX Keys S", TipoEquipamento.TECLADO);
            cadastrarModelo("MS", 1, 3, "Mouse Logitech MX Master 3S", TipoEquipamento.MOUSE);
            cadastrarModelo("CL", 1, 1, "Celular Samsung Galaxy S24", TipoEquipamento.CELULAR);
            cadastrarModelo("MD", 1, 1, "Mesa digitalizadora Wacom Intuos M", TipoEquipamento.MESA_DIGITALIZADORA);
            cadastrarModelo("WC", 1, 2, "Webcam Logitech Brio 4K", TipoEquipamento.OUTRO);
        }
    }

    //METODOS
    private void cadastrarPessoa(String nome, String email, String cargo, String senha, Perfil perfil) {
        if (colaboradorRepository.existsByEmail(email)) {
            return;
        }
        Colaborador pessoa = new Colaborador();
        pessoa.setNome(nome);
        pessoa.setEmail(email);
        pessoa.setCargo(cargo);
        pessoa.setSenha(passwordEncoder.encode(senha));
        pessoa.setPerfil(perfil);
        colaboradorRepository.save(pessoa);
    }

    private void cadastrarModelo(String prefixo, int primeiro, int quantidade, String nome, TipoEquipamento tipo) {
        for (int i = primeiro; i < primeiro + quantidade; i++) {
            Equipamento unidade = new Equipamento();
            unidade.setPatrimonio(String.format("%s-%03d", prefixo, i));
            unidade.setNome(nome);
            unidade.setTipo(tipo);
            equipamentoRepository.save(unidade);
        }
    }



}

package com.projeto.emprestimos.controller;

import com.projeto.emprestimos.dto.CadastroRequest;
import com.projeto.emprestimos.dto.LoginRequest;
import com.projeto.emprestimos.dto.TokenResposta;
import com.projeto.emprestimos.model.Colaborador;
import com.projeto.emprestimos.security.TokenService;
import com.projeto.emprestimos.service.ColaboradorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final ColaboradorService colaboradorService;

    public AuthController(
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            ColaboradorService colaboradorService
    ) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.colaboradorService = colaboradorService;
    }

    @PostMapping("/login")
    public TokenResposta login(@RequestBody @Valid LoginRequest dados) {
        // confere e-mail e senha com o BCrypt. Errou? AuthenticationException → 401 no TratadorDeErros
        Authentication autenticacao = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dados.email(), dados.senha())
        );

        // "ROLE_ADMIN" vira "ADMIN" pra ir dentro do token
        String perfilComPrefixo = autenticacao.getAuthorities().iterator().next().getAuthority();
        String perfil = perfilComPrefixo.replace("ROLE_", "");

        String token = tokenService.gerar(autenticacao.getName(), perfil);
        return new TokenResposta(token);
    }

    // criar conta: qualquer pessoa pode, sem token.
    // Já devolve o token, então a pessoa entra direto, sem fazer login de novo
    @PostMapping("/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResposta cadastro(@RequestBody @Valid CadastroRequest dados) {
        Colaborador novo = colaboradorService.criarConta(dados);

        String perfil = novo.getPerfil().name();
        String token = tokenService.gerar(novo.getEmail(), perfil);

        return new TokenResposta(token);
    }

    // quem sou eu? O front usa pra mostrar nome, e-mail e perfil de quem entrou
    @GetMapping("/eu")
    public Colaborador eu(Authentication autenticacao) {
        return colaboradorService.buscarPorEmail(autenticacao.getName());
    }

}

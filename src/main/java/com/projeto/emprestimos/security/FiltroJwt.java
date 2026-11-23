package com.projeto.emprestimos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// o porteiro
public class FiltroJwt extends OncePerRequestFilter {

    //ATRIBUTOS
    private final TokenService tokenService;

    //CONSTRUTOR
    public FiltroJwt(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    //MÉTODOS
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        String cabecalho = request.getHeader("Authorization");

        // só tenta ler se veio "Bearer " E alguma coisa depois (o "Bearer " sozinho tem 7 letras)
        boolean temCabecalho = cabecalho != null;
        boolean comecaComBearer = temCabecalho && cabecalho.startsWith("Bearer ");
        boolean temAlgoDepoisDoBearer = comecaComBearer && cabecalho.length() > 7;

        if (temAlgoDepoisDoBearer) {
            String token = cabecalho.substring(7);   // corta o "Bearer " e fica só o token

            try {
                Claims dados = tokenService.ler(token);
                String email = dados.getSubject();
                String perfil = dados.get("perfil", String.class);

                // o crachá interno do Spring (slide 42): quem, credenciais, perfis
                UsernamePasswordAuthenticationToken autenticacao = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + perfil))
                );

                SecurityContextHolder.getContext().setAuthentication(autenticacao);
            } catch (JwtException | IllegalArgumentException erro) {
                // token inválido, vencido ou vazio: segue sem login, e o SecurityConfig decide
            }
        }

        chain.doFilter(request, response);
    }
}
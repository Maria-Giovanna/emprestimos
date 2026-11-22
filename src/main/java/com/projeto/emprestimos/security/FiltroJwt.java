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

public class FiltroJwt extends OncePerRequestFilter {
    private final TokenService tokenService;

    public FiltroJwt(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        String cabecalho = request.getHeader("Authorization");
        boolean temToken = cabecalho != null && cabecalho.startsWith("Bearer ");

        if (temToken) {
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
            } catch (JwtException jwte) {}
        }
        chain.doFilter(request, response);
    }
}
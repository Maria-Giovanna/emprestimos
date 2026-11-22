package com.projeto.emprestimos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class TokenService {

    //ATRIBUTOS
    private static final long DEZ_MINUTOS = 10 * 60 * 1000; // 10 min × 60 s × 1000 ms
    private final SecretKey chave;


    //CONSTRUTOR
    public TokenService(@Value("${jwt.segredo}") String segredo) {
        this.chave = Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
    }


    //MÉTODOS
    public String gerar(String usuario, String perfil) {
        Date agora = new Date();
        Date vencimento = new Date(agora.getTime() + DEZ_MINUTOS);

        return Jwts.builder()
                .subject(usuario)
                .claim("perfil", perfil)
                .issuedAt(agora)
                .expiration(vencimento)
                .signWith(chave)
                .compact();
    }

    public Claims ler(String token) {
        return Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
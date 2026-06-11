package com.progweb.biterate.security;

import com.progweb.biterate.model.Cliente;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class Jwtservice {

    // Injeta a chave secreta definida no application.properties
    @Value("${jwt.secret}")
    private String secret;

    // Injeta o tempo de expiração do token em milissegundos
    @Value("${jwt.expiration}")
    private Long expiration;

    // Decodifica a assinatura secreta e gera a chave criptográfica
    private Key getKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Cria o token JWT com email do usuário, data de criação e expiração
    public String gerarToken(Cliente cliente) {
        return Jwts.builder()
                .setSubject(cliente.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extrai o email (subject) de dentro do token
    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    // Valida se o token pertence ao usuário logado e não expirou
    public boolean tokenValido(String token, Cliente cliente) {
        String email = extrairEmail(token);
        return email.equals(cliente.getEmail()) && !tokenExpirado(token);
    }

    // Verifica se a data de expiração do token é anterior ao momento atual
    private boolean tokenExpirado(String token) {
        return extrairClaims(token).getExpiration().before(new Date());
    }

    // Decodifica e extrai todos os dados (Claims) salvos no token usando a chave secreta
    private Claims extrairClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
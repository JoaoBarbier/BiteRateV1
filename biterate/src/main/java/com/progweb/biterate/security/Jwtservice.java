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
 
    @Value("${jwt.secret}")
    private String secret;
 
    @Value("${jwt.expiration}")
    private Long expiration;
 
    private Key getKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
 
    public String gerarToken(Cliente cliente) {
        return Jwts.builder()
                .setSubject(cliente.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }
 
    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }
 
    public boolean tokenValido(String token, Cliente cliente) {
        String email = extrairEmail(token);
        return email.equals(cliente.getEmail()) && !tokenExpirado(token);
    }
 
    private boolean tokenExpirado(String token) {
        return extrairClaims(token).getExpiration().before(new Date());
    }
 
    private Claims extrairClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
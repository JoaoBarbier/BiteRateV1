package com.progweb.biterate.security;

import com.progweb.biterate.model.Cliente;
import com.progweb.biterate.repository.Clienterepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class Jwtfilter extends OncePerRequestFilter { // Executado uma vez por requisição

    private final Jwtservice jwtservice;
    private final Clienterepository clienterepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Pega o cabeçalho de autorização
        final String authHeader = request.getHeader("Authorization");

        // Se não tiver token, segue para o próximo filtro e barra depois se o endpoint for protegido
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrai o token pulando o "Bearer " (7 caracteres)
        final String token = authHeader.substring(7);
        final String email = jwtservice.extrairEmail(token);

        // Se achou o email e o usuário ainda não está autenticado no contexto do Spring
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Cliente cliente = clienterepository.findByEmail(email).orElse(null);

            // Valida se o token pertence ao usuário e não expirou
            if (cliente != null && jwtservice.tokenValido(token, cliente)) {
                // Cria o objeto de autenticação com o usuário e suas permissões
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                cliente,
                                null,
                                cliente.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Salva o usuário no contexto de segurança do Spring (loga o usuário na requisição)
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continua o fluxo da requisição
        filterChain.doFilter(request, response);
    }
}
package com.progweb.biterate.service;

import com.progweb.biterate.dto.request.CadastroRequest;
import com.progweb.biterate.dto.request.LoginRequest;
import com.progweb.biterate.dto.response.ClienteResponse;
import com.progweb.biterate.dto.response.LoginResponse;
import com.progweb.biterate.exception.ConflitoException;
import com.progweb.biterate.exception.NaoEncontradoException;
import com.progweb.biterate.model.Cliente;
import com.progweb.biterate.repository.Avaliacaorepository;
import com.progweb.biterate.repository.Clienterepository;
import com.progweb.biterate.security.Jwtservice;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Authservice {

    private final Clienterepository clienteRepository;
    private final Avaliacaorepository avaliacaoRepository;
    private final PasswordEncoder passwordEncoder;
    private final Jwtservice jwtService;
    private final AuthenticationManager authenticationManager;

    public ClienteResponse cadastrar(CadastroRequest request) {
        if (!request.getSenha().equals(request.getConfirmarSenha())) {
            throw new RuntimeException("As senhas não coincidem");
        }
        if (clienteRepository.existsByEmail(request.getEmail())) {
            throw new ConflitoException("E-mail já cadastrado");
        }
        if (clienteRepository.existsByUsername(request.getUsername())) {
            throw new ConflitoException("Username já está em uso");
        }

        Cliente cliente = Cliente.builder()
                .nome(request.getNome())
                .sobrenome(request.getSobrenome())
                .username(request.getUsername())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .build();

        clienteRepository.save(cliente);
        return toResponse(cliente);
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        Cliente cliente = clienteRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NaoEncontradoException("Cliente não encontrado"));

        String token = jwtService.gerarToken(cliente);
        return new LoginResponse(token, toResponse(cliente));
    }

    private ClienteResponse toResponse(Cliente cliente) {
        int totalAvaliacoes = (int) avaliacaoRepository.countByClienteId(cliente.getId());
        int totalFavoritos  = (int) clienteRepository.countFavoritosByClienteId(cliente.getId());
        return ClienteResponse.builder()
                .id(cliente.getId())
                .nome(cliente.getNome())
                .sobrenome(cliente.getSobrenome())
                .username(cliente.getUsernameApelido())
                .email(cliente.getEmail())
                .bio(cliente.getBio())
                .cidade(cliente.getCidade())
                .fotoUrl(cliente.getFotoUrl())
                .totalAvaliacoes(totalAvaliacoes)
                .totalFavoritos(totalFavoritos)
                .criadoEm(cliente.getCriadoEm())
                .build();
    }
}

package com.progweb.biterate.service;

import com.progweb.biterate.dto.request.EditarPerfilRequest;
import com.progweb.biterate.dto.response.AvaliacaoResponse;
import com.progweb.biterate.dto.response.ClienteResponse;
import com.progweb.biterate.dto.response.RestauranteResumoResponse;
import com.progweb.biterate.exception.ConflitoException;
import com.progweb.biterate.exception.NaoEncontradoException;
import com.progweb.biterate.model.Avaliacao;
import com.progweb.biterate.model.Cliente;
import com.progweb.biterate.repository.Avaliacaorepository;
import com.progweb.biterate.repository.Clienterepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Clienteservice {

    private final Clienterepository clienteRepository;
    private final Avaliacaorepository avaliacaoRepository;

    // Retorna os dados do perfil obtidos diretamente do token autenticado
    public ClienteResponse buscarPerfil(Cliente clienteAutenticado) {
        return toResponse(clienteAutenticado);
    }

    // Atualiza os dados cadastrais do cliente, bloqueando usernames duplicados
    public ClienteResponse editarPerfil(Cliente clienteAutenticado, EditarPerfilRequest request) {
        // Se mudou o username, valida se o novo já pertence a outra pessoa
        if (!clienteAutenticado.getUsernameApelido().equals(request.getUsername())
                && clienteRepository.existsByUsername(request.getUsername())) {
            throw new ConflitoException("Username já está em uso");
        }

        clienteAutenticado.setNome(request.getNome());
        clienteAutenticado.setSobrenome(request.getSobrenome());
        clienteAutenticado.setUsername(request.getUsername());
        clienteAutenticado.setBio(request.getBio());
        clienteAutenticado.setCidade(request.getCidade());

        clienteRepository.save(clienteAutenticado);
        return toResponse(clienteAutenticado);
    }

    // Lista todas as avaliações que o usuário autenticado já fez na plataforma
    public List<AvaliacaoResponse> listarAvaliacoes(Cliente clienteAutenticado) {
        return avaliacaoRepository
                .findByClienteIdOrderByCriadoEmDesc(clienteAutenticado.getId())
                .stream()
                .map(this::toAvaliacaoResponse)
                .toList();
    }

    // Busca a lista de restaurantes favoritados pelo usuário autenticado (Transação somente leitura)
    @Transactional(readOnly = true)
    public List<RestauranteResumoResponse> listarFavoritos(Cliente clienteAutenticado) {
        Cliente cliente = clienteRepository.findById(clienteAutenticado.getId())
                .orElseThrow(() -> new NaoEncontradoException("Cliente não encontrado"));

        return cliente.getFavoritos().stream()
                .map(r -> RestauranteResumoResponse.builder()
                        .id(r.getId())
                        .nome(r.getNome())
                        .categoria(r.getCategoria())
                        .faixaPreco(r.getFaixaPreco())
                        .bairro(r.getBairro())
                        .cidade(r.getCidade())
                        .fotoUrl(r.getFotoUrl())
                        .mediaNote(r.getMediaNote())
                        .totalAvaliacoes(r.getTotalAvaliacoes())
                        .build())
                .toList();
    }

    // Mapeia os dados da entidade Cliente e calcula os totais para o DTO de resposta
    private ClienteResponse toResponse(Cliente cliente) {
        int totalAvaliacoes = (int) avaliacaoRepository.countByClienteId(cliente.getId());
        int totalFavoritos = (int) clienteRepository.countFavoritosByClienteId(cliente.getId());
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

    // Mapeia uma entidade interna Avaliacao para o seu DTO de resposta correspondente
    private AvaliacaoResponse toAvaliacaoResponse(Avaliacao avaliacao) {
        return AvaliacaoResponse.builder()
                .id(avaliacao.getId())
                .nota(avaliacao.getNota())
                .titulo(avaliacao.getTitulo())
                .comentario(avaliacao.getComentario())
                .dataVisita(avaliacao.getDataVisita())
                .tipoVisita(avaliacao.getTipoVisita())
                .criadoEm(avaliacao.getCriadoEm())
                .clienteId(avaliacao.getCliente().getId())
                .clienteNome(avaliacao.getCliente().getNome())
                .clienteUsername(avaliacao.getCliente().getUsernameApelido())
                .clienteFotoUrl(avaliacao.getCliente().getFotoUrl())
                .restauranteId(avaliacao.getRestaurante().getId())
                .restauranteNome(avaliacao.getRestaurante().getNome())
                .build();
    }
}
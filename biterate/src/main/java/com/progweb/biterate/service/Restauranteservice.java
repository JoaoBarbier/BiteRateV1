package com.progweb.biterate.service;

import com.progweb.biterate.dto.request.RestauranteRequest;
import com.progweb.biterate.dto.response.AvaliacaoResponse;
import com.progweb.biterate.dto.response.HorarioResponse;
import com.progweb.biterate.dto.response.RestauranteResponse;
import com.progweb.biterate.exception.ConflitoException;
import com.progweb.biterate.exception.NaoEncontradoException;
import com.progweb.biterate.model.Avaliacao;
import com.progweb.biterate.model.Cliente;
import com.progweb.biterate.model.HorarioFuncionamento;
import com.progweb.biterate.model.Restaurante;
import com.progweb.biterate.repository.Avaliacaorepository;
import com.progweb.biterate.repository.Avaliacaorepository.NotaContagem;
import com.progweb.biterate.repository.Clienterepository;
import com.progweb.biterate.repository.HorarioFuncionamentorepository;
import com.progweb.biterate.repository.Restauranterepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Restauranteservice {

    private final Restauranterepository restauranteRepository;
    private final Avaliacaorepository avaliacaoRepository;
    private final Clienterepository clienteRepository;
    private final HorarioFuncionamentorepository horarioRepository;

    public RestauranteResponse buscarPorId(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Restaurante não encontrado"));
        return toResponse(restaurante);
    }

    public Page<AvaliacaoResponse> listarAvaliacoes(Long restauranteId, int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("criadoEm").descending());
        return avaliacaoRepository
                .findByRestauranteId(restauranteId, pageable)
                .map(this::toAvaliacaoResponse);
    }

    @Transactional
    public void favoritar(Long restauranteId, Cliente clienteAutenticado) {
        Cliente cliente = clienteRepository.findById(clienteAutenticado.getId())
                .orElseThrow(() -> new NaoEncontradoException("Cliente não encontrado"));
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new NaoEncontradoException("Restaurante não encontrado"));

        if (cliente.getFavoritos().contains(restaurante)) {
            cliente.getFavoritos().remove(restaurante);
        } else {
            cliente.getFavoritos().add(restaurante);
        }
        clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public boolean isFavoritado(Long restauranteId, Cliente clienteAutenticado) {
        Cliente cliente = clienteRepository.findById(clienteAutenticado.getId())
                .orElseThrow(() -> new NaoEncontradoException("Cliente não encontrado"));
        return cliente.getFavoritos().stream()
                .anyMatch(r -> r.getId().equals(restauranteId));
    }

    @Transactional
    public RestauranteResponse cadastrar(RestauranteRequest request) {
        if (restauranteRepository.existsByRuaAndNumeroAndCidade(
                request.getRua(), request.getNumero(), request.getCidade())) {
            throw new ConflitoException("Já existe um restaurante cadastrado nesse endereço.");
        }

        Restaurante restaurante = Restaurante.builder()
                .nome(request.getNome())
                .categoria(request.getCategoria())
                .descricao(request.getDescricao())
                .faixaPreco(request.getFaixaPreco())
                .cep(request.getCep())
                .estado(request.getEstado())
                .rua(request.getRua())
                .numero(request.getNumero())
                .bairro(request.getBairro())
                .cidade(request.getCidade())
                .complemento(request.getComplemento())
                .telefone(request.getTelefone())
                .comodidades(request.getComodidades())
                .fotoUrl(request.getFotoUrl())
                .build();

        restauranteRepository.save(restaurante);

        if (request.getHorarios() != null) {
            List<HorarioFuncionamento> horarios = request.getHorarios().stream()
                    .map(h -> HorarioFuncionamento.builder()
                            .dia(h.getDia())
                            .aberto(h.isAberto())
                            .horaAbertura(h.isAberto() ? h.getHoraAbertura() : null)
                            .horaFechamento(h.isAberto() ? h.getHoraFechamento() : null)
                            .restaurante(restaurante)
                            .build())
                    .toList();
            horarioRepository.saveAll(horarios);
        }

        return toResponse(restaurante);
    }

    public void recalcularMedia(Long restauranteId) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new NaoEncontradoException("Restaurante não encontrado"));

        Double media = avaliacaoRepository.calcularMediaNota(restauranteId);
        long total = avaliacaoRepository.countByRestauranteId(restauranteId);

        restaurante.setMediaNote(media != null ? Math.round(media * 10.0) / 10.0 : 0.0);
        restaurante.setTotalAvaliacoes((int) total);
        restauranteRepository.save(restaurante);
    }

    public RestauranteResponse toResponse(Restaurante r) {
        List<HorarioResponse> horarios = horarioRepository.findByRestauranteId(r.getId())
                .stream()
                .map(h -> HorarioResponse.builder()
                        .dia(h.getDia())
                        .aberto(h.isAberto())
                        .horaAbertura(h.getHoraAbertura())
                        .horaFechamento(h.getHoraFechamento())
                        .build())
                .toList();

        return RestauranteResponse.builder()
                .id(r.getId())
                .nome(r.getNome())
                .categoria(r.getCategoria())
                .descricao(r.getDescricao())
                .faixaPreco(r.getFaixaPreco())
                .cep(r.getCep())
                .estado(r.getEstado())
                .rua(r.getRua())
                .numero(r.getNumero())
                .bairro(r.getBairro())
                .cidade(r.getCidade())
                .complemento(r.getComplemento())
                .telefone(r.getTelefone())
                .comodidades(r.getComodidades())
                .fotoUrl(r.getFotoUrl())
                .mediaNote(r.getMediaNote())
                .totalAvaliacoes(r.getTotalAvaliacoes())
                .criadoEm(r.getCriadoEm())
                .horarios(horarios)
                .distribuicaoNotas(calcularDistribuicao(r.getId()))
                .build();
    }

    private Map<Integer, Long> calcularDistribuicao(Long restauranteId) {
        Map<Integer, Long> dist = new HashMap<>();
        for (int i = 1; i <= 5; i++) dist.put(i, 0L);
        avaliacaoRepository.contarPorNota(restauranteId)
                .forEach(item -> dist.put(item.getNota(), item.getContagem()));
        return dist;
    }

    private AvaliacaoResponse toAvaliacaoResponse(Avaliacao a) {
        return AvaliacaoResponse.builder()
                .id(a.getId())
                .nota(a.getNota())
                .titulo(a.getTitulo())
                .comentario(a.getComentario())
                .dataVisita(a.getDataVisita())
                .tipoVisita(a.getTipoVisita())
                .criadoEm(a.getCriadoEm())
                .clienteId(a.getCliente().getId())
                .clienteNome(a.getCliente().getNome())
                .clienteUsername(a.getCliente().getUsernameApelido())
                .clienteFotoUrl(a.getCliente().getFotoUrl())
                .restauranteId(a.getRestaurante().getId())
                .restauranteNome(a.getRestaurante().getNome())
                .build();
    }
}

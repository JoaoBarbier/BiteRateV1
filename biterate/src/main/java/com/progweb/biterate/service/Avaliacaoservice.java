package com.progweb.biterate.service;

import com.progweb.biterate.dto.request.AvaliacaoRequest;
import com.progweb.biterate.dto.response.AvaliacaoResponse;
import com.progweb.biterate.exception.ConflitoException;
import com.progweb.biterate.exception.NaoEncontradoException;
import com.progweb.biterate.exception.SemPermissaoException;
import com.progweb.biterate.model.Avaliacao;
import com.progweb.biterate.model.Cliente;
import com.progweb.biterate.model.Restaurante;
import com.progweb.biterate.repository.Avaliacaorepository;
import com.progweb.biterate.repository.Restauranterepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class Avaliacaoservice {

    private final Avaliacaorepository avaliacaoRepository;
    private final Restauranterepository restauranteRepository;
    private final Restauranteservice restauranteService;

    public Page<AvaliacaoResponse> listarRecentes(int size) {
        return avaliacaoRepository
                .findAllByOrderByCriadoEmDesc(PageRequest.of(0, size))
                .map(this::toResponse);
    }

    @Transactional
    public AvaliacaoResponse criar(AvaliacaoRequest request, Cliente clienteAutenticado) {
        Restaurante restaurante = restauranteRepository.findById(request.getRestauranteId())
                .orElseThrow(() -> new NaoEncontradoException("Restaurante não encontrado"));

        if (avaliacaoRepository.existsByClienteIdAndRestauranteId(
                clienteAutenticado.getId(), restaurante.getId())) {
            throw new ConflitoException("Você já avaliou este restaurante");
        }

        Avaliacao avaliacao = Avaliacao.builder()
                .nota(request.getNota())
                .notaComida(request.getNotaComida())
                .notaAtendimento(request.getNotaAtendimento())
                .notaAmbiente(request.getNotaAmbiente())
                .titulo(request.getTitulo())
                .comentario(request.getComentario())
                .dataVisita(request.getDataVisita())
                .tipoVisita(request.getTipoVisita())
                .cliente(clienteAutenticado)
                .restaurante(restaurante)
                .build();

        avaliacaoRepository.save(avaliacao);
        restauranteService.recalcularMedia(restaurante.getId());

        return toResponse(avaliacao);
    }

    @Transactional
    public AvaliacaoResponse editar(Long id, AvaliacaoRequest request, Cliente clienteAutenticado) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Avaliação não encontrada"));

        if (!avaliacao.getCliente().getId().equals(clienteAutenticado.getId())) {
            throw new SemPermissaoException("Você não tem permissão para editar esta avaliação");
        }

        avaliacao.setNota(request.getNota());
        avaliacao.setNotaComida(request.getNotaComida());
        avaliacao.setNotaAtendimento(request.getNotaAtendimento());
        avaliacao.setNotaAmbiente(request.getNotaAmbiente());
        avaliacao.setTitulo(request.getTitulo());
        avaliacao.setComentario(request.getComentario());
        avaliacao.setDataVisita(request.getDataVisita());
        avaliacao.setTipoVisita(request.getTipoVisita());

        avaliacaoRepository.save(avaliacao);
        restauranteService.recalcularMedia(avaliacao.getRestaurante().getId());

        return toResponse(avaliacao);
    }

    @Transactional
    public void excluir(Long id, Cliente clienteAutenticado) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Avaliação não encontrada"));

        if (!avaliacao.getCliente().getId().equals(clienteAutenticado.getId())) {
            throw new SemPermissaoException("Você não tem permissão para excluir esta avaliação");
        }

        Long restauranteId = avaliacao.getRestaurante().getId();
        avaliacaoRepository.delete(avaliacao);
        restauranteService.recalcularMedia(restauranteId);
    }

    public AvaliacaoResponse toResponse(Avaliacao a) {
        return AvaliacaoResponse.builder()
                .id(a.getId())
                .nota(a.getNota())
                .notaComida(a.getNotaComida())
                .notaAtendimento(a.getNotaAtendimento())
                .notaAmbiente(a.getNotaAmbiente())
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

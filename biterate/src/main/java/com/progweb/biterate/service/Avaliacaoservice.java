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

    // Busca as avaliações mais recentes ordenadas por data de criação
    public Page<AvaliacaoResponse> listarRecentes(int size) {
        return avaliacaoRepository
                .findAllByOrderByCriadoEmDesc(PageRequest.of(0, size))
                .map(this::toResponse);
    }

    // Salva uma nova avaliação e atualiza a nota média do restaurante
    @Transactional
    public AvaliacaoResponse criar(AvaliacaoRequest request, Cliente clienteAutenticado) {
        Restaurante restaurante = restauranteRepository.findById(request.getRestauranteId())
                .orElseThrow(() -> new NaoEncontradoException("Restaurante não encontrado"));

        // Bloqueia se o usuário já avaliou esse mesmo restaurante antes
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
        restauranteService.recalcularMedia(restaurante.getId()); // Atualiza média do restaurante

        return toResponse(avaliacao);
    }

    // Modifica os dados de uma avaliação caso o usuário logado seja o dono dela
    @Transactional
    public AvaliacaoResponse editar(Long id, AvaliacaoRequest request, Cliente clienteAutenticado) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Avaliação não encontrada"));

        // Valida se quem está tentando editar é realmente o autor da avaliação
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
        restauranteService.recalcularMedia(avaliacao.getRestaurante().getId()); // Recalcula a média após alteração

        return toResponse(avaliacao);
    }

    // Apaga a avaliação se o usuário for o dono e corrige a média do restaurante
    @Transactional
    public void excluir(Long id, Cliente clienteAutenticado) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("Avaliação não encontrada"));

        // Valida propriedade antes de deletar
        if (!avaliacao.getCliente().getId().equals(clienteAutenticado.getId())) {
            throw new SemPermissaoException("Você não tem permissão para excluir esta avaliação");
        }

        Long restauranteId = avaliacao.getRestaurante().getId();
        avaliacaoRepository.delete(avaliacao);
        restauranteService.recalcularMedia(restauranteId); // Recalcula a média após remoção
    }

    // Converte a entidade interna Avaliacao para o DTO de saída do sistema
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
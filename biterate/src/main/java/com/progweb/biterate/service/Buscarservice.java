package com.progweb.biterate.service;

import com.progweb.biterate.dto.request.BuscarRestauranteRequest;
import com.progweb.biterate.dto.response.RestauranteResumoResponse;
import com.progweb.biterate.repository.Restauranterepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Buscarservice {

    private final Restauranterepository restauranteRepository;

    public Page<RestauranteResumoResponse> buscarComComodidades(BuscarRestauranteRequest request) {
        Pageable pageable = resolverOrdenacao(request);
        List<String> c = request.getComodidades();

        String c1 = (c != null && c.size() > 0) ? c.get(0) : null;
        String c2 = (c != null && c.size() > 1) ? c.get(1) : null;
        String c3 = (c != null && c.size() > 2) ? c.get(2) : null;
        String c4 = (c != null && c.size() > 3) ? c.get(3) : null;
        String c5 = (c != null && c.size() > 4) ? c.get(4) : null;

        LocalDateTime agora = LocalDateTime.now();
        String diaAtual = resolverDia(agora.getDayOfWeek());
        String horaAtual = agora.format(DateTimeFormatter.ofPattern("HH:mm"));

        return restauranteRepository
                .buscarComFiltros(
                        request.getTermo(),
                        request.getCategorias(),
                        request.getNotaMinima(),
                        request.getFaixasPreco(),
                        c1, c2, c3, c4, c5,
                        request.isApenasAbertos(),
                        diaAtual,
                        horaAtual,
                        pageable
                )
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
                        .build());
    }

    private String resolverDia(DayOfWeek dow) {
        return switch (dow) {
            case MONDAY    -> "seg";
            case TUESDAY   -> "ter";
            case WEDNESDAY -> "qua";
            case THURSDAY  -> "qui";
            case FRIDAY    -> "sex";
            case SATURDAY  -> "sab";
            case SUNDAY    -> "dom";
        };
    }

    private Pageable resolverOrdenacao(BuscarRestauranteRequest request) {
        Sort sort = switch (request.getOrdenacao() != null ? request.getOrdenacao() : "relevancia") {
            case "nota"       -> Sort.by("mediaNote").descending();
            case "avaliacoes" -> Sort.by("totalAvaliacoes").descending();
            case "recentes"   -> Sort.by("criadoEm").descending();
            default           -> Sort.by("mediaNote").descending();
        };
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }
}

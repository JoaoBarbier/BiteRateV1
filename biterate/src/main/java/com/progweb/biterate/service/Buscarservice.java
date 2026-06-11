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

    // Executa a busca complexa aplicando filtros, ordenação, paginação e status de funcionamento (aberto/fechado)
    public Page<RestauranteResumoResponse> buscarComComodidades(BuscarRestauranteRequest request) {
        Pageable pageable = resolverOrdenacao(request);
        List<String> c = request.getComodidades();

        // Quebra a lista de comodidades em variáveis individuais para passar direto na Query nativa do Repository
        String c1 = (c != null && c.size() > 0) ? c.get(0) : null;
        String c2 = (c != null && c.size() > 1) ? c.get(1) : null;
        String c3 = (c != null && c.size() > 2) ? c.get(2) : null;
        String c4 = (c != null && c.size() > 3) ? c.get(3) : null;
        String c5 = (c != null && c.size() > 4) ? c.get(4) : null;

        // Pega a hora e o dia da semana atual para validar se o restaurante está aberto agora
        LocalDateTime agora = LocalDateTime.now();
        String diaAtual = resolverDia(agora.getDayOfWeek());
        String horaAtual = agora.format(DateTimeFormatter.ofPattern("HH:mm"));

        // Faz a consulta no banco de dados e converte o resultado para o DTO de resumo
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

    // Converte o enum DayOfWeek do Java para a string de 3 letras que o banco de dados espera
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

    // Define a direção e o campo de ordenação com base no parâmetro recebido da requisição
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
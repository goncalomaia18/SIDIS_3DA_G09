package pt.isep.sidis.airportsroutes.application;

import pt.isep.sidis.airportsroutes.application.dto.AlternativeRouteDTO;
import java.util.List;

public interface RouteSearchStrategy {

    /**
     * Procura rotas alternativas entre dois aeroportos.
     * @param originIata Código IATA do aeroporto de partida
     * @param destinationIata Código IATA do aeroporto de chegada
     * @return Lista de viagens alternativas possíveis
     */
    List<AlternativeRouteDTO> findAlternatives(String originIata, String destinationIata);

}
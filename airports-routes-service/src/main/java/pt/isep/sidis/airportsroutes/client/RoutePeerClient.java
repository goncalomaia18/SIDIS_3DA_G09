
package pt.isep.sidis.airportsroutes.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;
import pt.isep.sidis.airportsroutes.application.dto.RouteViewDTO;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
public class RoutePeerClient {

    private final RestClient restClient;
    private final List<String> peers;

    public RoutePeerClient(
            @Value("${airports.routes.peers:}") String peersConfig,
            @Value("${spring.security.user.name:user}") String username,
            @Value("${spring.security.user.password:Sidis123!}") String password) {

        this.peers = Arrays.stream(peersConfig.split(","))
                .map(String::trim)
                .filter(peer -> !peer.isEmpty())
                .map(peer -> peer.replaceAll("/+$", ""))
                .toList();

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(3));

        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .defaultHeaders(headers ->
                        headers.setBasicAuth(username, password))
                .build();
    }

    public RouteViewDTO findRouteOnPeers(String routeId) {

        boolean unavailablePeer = false;

        for (String peer : peers) {

            for (int attempt = 1; attempt <= 2; attempt++) {

                try {
                    RouteViewDTO route = restClient.get()
                            .uri(peer + "/api/routes/local/{id}", routeId)
                            .retrieve()
                            .onStatus(
                                    status -> status.value() == 404,
                                    (request, response) -> {
                                        throw new ResponseStatusException(
                                                HttpStatus.NOT_FOUND
                                        );
                                    }
                            )
                            .onStatus(
                                    HttpStatusCode::isError,
                                    (request, response) -> {
                                        throw new IllegalStateException(
                                                "Peer returned HTTP "
                                                        + response.getStatusCode()
                                        );
                                    }
                            )
                            .body(RouteViewDTO.class);

                    if (route != null) {
                        System.out.println(
                                "Route " + routeId
                                        + " found on peer " + peer
                        );
                        return route;
                    }

                    break;

                } catch (ResponseStatusException e) {

                    if (e.getStatusCode().value() == 404) {
                        break;
                    }

                    throw e;

                } catch (RestClientException | IllegalStateException e) {

                    unavailablePeer = true;

                    System.err.println(
                            "Peer " + peer
                                    + " failed (attempt " + attempt
                                    + "): " + e.getMessage()
                    );
                }
            }
        }

        if (unavailablePeer) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "One or more route peers are unavailable"
            );
        }

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Route not found: " + routeId
        );
    }
}

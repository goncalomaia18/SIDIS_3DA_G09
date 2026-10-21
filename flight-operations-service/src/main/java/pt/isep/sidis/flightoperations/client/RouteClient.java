
package pt.isep.sidis.flightoperations.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import pt.isep.sidis.flightoperations.application.dto.RouteResponseDTO;

import java.time.Duration;

@Component
public class RouteClient {

    private final RestClient restClient;

    public RouteClient(
            @Value("${services.airports-routes.url:http://localhost:8083}") String baseUrl,
            @Value("${services.airports-routes.username:user}") String username,
            @Value("${services.airports-routes.password:}") String password) {

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .requestFactory(createRequestFactory())
                .build();
    }

    private org.springframework.http.client.SimpleClientHttpRequestFactory createRequestFactory() {
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));
        return factory;
    }

    public RouteResponseDTO getRouteById(String routeId) {

        try {
            return restClient.get()
                    .uri("/api/routes/{id}", routeId)
                    .retrieve()
                    .onStatus(
                            status -> status.value() == 404,
                            (request, response) -> {
                                throw new IllegalArgumentException(
                                        "Route not found: " + routeId
                                );
                            }
                    )
                    .body(RouteResponseDTO.class);

        } catch (RestClientException e) {
            throw new IllegalStateException(
                    "Unable to communicate with Airports & Routes service: "
                            + e.getMessage(),
                    e
            );
        }
    }
}

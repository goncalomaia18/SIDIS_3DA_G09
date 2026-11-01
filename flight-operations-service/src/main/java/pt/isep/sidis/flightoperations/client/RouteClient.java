
package pt.isep.sidis.flightoperations.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import pt.isep.sidis.flightoperations.application.dto.RouteResponseDTO;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
public class RouteClient {

    private final RestClient restClient;
    private final List<String> serviceUrls;

    public RouteClient(
            @Value("${services.airports-routes.urls:http://localhost:8083,http://localhost:8084}") String urls,
            @Value("${services.airports-routes.username:user}") String username,
            @Value("${services.airports-routes.password:Sidis123!}") String password) {

        this.serviceUrls = Arrays.stream(urls.split(","))
                .map(String::trim)
                .filter(url -> !url.isEmpty())
                .map(url -> url.replaceAll("/+$", ""))
                .toList();

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(3));

        this.restClient = RestClient.builder()
                .requestFactory(factory)
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();
    }

    public RouteResponseDTO getRouteById(String routeId) {

        for (String serviceUrl : serviceUrls) {

            for (int attempt = 1; attempt <= 2; attempt++) {

                try {
                    RouteResponseDTO route = restClient.get()
                            .uri(serviceUrl + "/api/routes/{id}", routeId)
                            .retrieve()
                            .onStatus(
                                    status -> status.value() == 404,
                                    (request, response) -> {
                                        throw new IllegalArgumentException(
                                                "Route not found: " + routeId
                                        );
                                    }
                            )
                            .onStatus(
                                    HttpStatusCode::isError,
                                    (request, response) -> {
                                        throw new IllegalStateException(
                                                "Airports & Routes returned HTTP "
                                                        + response.getStatusCode()
                                        );
                                    }
                            )
                            .body(RouteResponseDTO.class);

                    if (route != null) {
                        System.out.println(
                                "Route " + routeId
                                        + " retrieved from " + serviceUrl
                        );
                        return route;
                    }

                    break;

                } catch (IllegalArgumentException e) {
                    throw e;

                } catch (RestClientException | IllegalStateException e) {
                    System.err.println(
                            "Airports & Routes " + serviceUrl
                                    + " failed (attempt " + attempt
                                    + "): " + e.getMessage()
                    );
                }
            }
        }

        throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
                "All Airports & Routes instances are unavailable"
        );
    }
}

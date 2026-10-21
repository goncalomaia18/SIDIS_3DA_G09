
package pt.isep.sidis.flightoperations.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import pt.isep.sidis.flightoperations.application.dto.AircraftResponseDTO;

import java.time.Duration;

@Component
public class AircraftClient {

    private final RestClient restClient;

    public AircraftClient(
            @Value("${services.aircraft-maintenance.url:http://localhost:8081}")
            String baseUrl,

            @Value("${services.aircraft-maintenance.username:user}")
            String username,

            @Value("${services.aircraft-maintenance.password}")
            String password) {

        var requestFactory =
                new org.springframework.http.client.SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeaders(headers ->
                        headers.setBasicAuth(username, password))
                .requestFactory(requestFactory)
                .build();
    }

    public AircraftResponseDTO getAircraftByRegistrationNumber(
            String registrationNumber) {

        try {
            return restClient.get()
                    .uri("/api/aircraft-models/instances/{registrationNumber}",
                            registrationNumber)
                    .retrieve()
                    .onStatus(status -> status.value() == 404,
                            (request, response) -> {
                                throw new IllegalArgumentException(
                                        "Aircraft not found: " + registrationNumber
                                );
                            })
                    .body(AircraftResponseDTO.class);

        } catch (RestClientException e) {
            throw new IllegalStateException(
                    "Unable to communicate with Aircraft & Maintenance: "
                            + e.getMessage(),
                    e
            );
        }
    }
}

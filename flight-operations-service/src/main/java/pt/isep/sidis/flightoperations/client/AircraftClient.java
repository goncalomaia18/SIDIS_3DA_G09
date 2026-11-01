
package pt.isep.sidis.flightoperations.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import pt.isep.sidis.flightoperations.application.dto.AircraftResponseDTO;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
public class AircraftClient {

    private final RestClient restClient;
    private final List<String> serviceUrls;

    public AircraftClient(
            @Value("${services.aircraft-maintenance.urls:http://localhost:8081,http://localhost:8085}")
            String urls,

            @Value("${services.aircraft-maintenance.username:user}")
            String username,

            @Value("${services.aircraft-maintenance.password:Sidis123!}")
            String password) {

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
                .defaultHeaders(headers ->
                        headers.setBasicAuth(username, password))
                .build();
    }

    public AircraftResponseDTO getAircraftByRegistrationNumber(
            String registrationNumber) {

        boolean receivedNotFound = false;

        for (String serviceUrl : serviceUrls) {

            for (int attempt = 1; attempt <= 2; attempt++) {

                try {
                    AircraftResponseDTO aircraft = restClient.get()
                            .uri(
                                    serviceUrl
                                            + "/api/aircraft-models/instances/{registrationNumber}",
                                    registrationNumber
                            )
                            .retrieve()
                            .onStatus(
                                    status -> status.value() == 404,
                                    (request, response) -> {
                                        throw new IllegalArgumentException(
                                                "Aircraft not found: "
                                                        + registrationNumber
                                        );
                                    }
                            )
                            .onStatus(
                                    HttpStatusCode::isError,
                                    (request, response) -> {
                                        throw new IllegalStateException(
                                                "Aircraft & Maintenance returned HTTP "
                                                        + response.getStatusCode()
                                        );
                                    }
                            )
                            .body(AircraftResponseDTO.class);

                    if (aircraft != null) {
                        System.out.println(
                                "Aircraft " + registrationNumber
                                        + " retrieved from " + serviceUrl
                        );
                        return aircraft;
                    }

                    break;

                } catch (IllegalArgumentException e) {
                    receivedNotFound = true;
                    break;

                } catch (RestClientException | IllegalStateException e) {
                    System.err.println(
                            "Aircraft & Maintenance " + serviceUrl
                                    + " failed (attempt " + attempt
                                    + "): " + e.getMessage()
                    );
                }
            }
        }

        if (receivedNotFound) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Aircraft not found: " + registrationNumber
            );
        }

        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "All Aircraft & Maintenance instances are unavailable"
        );
    }
}


package pt.isep.sidis.aircraftmaintenance.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import pt.isep.sidis.aircraftmaintenance.application.dto.AircraftViewDTO;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
public class AircraftPeerClient {

    private final RestClient restClient;
    private final List<String> peers;

    public AircraftPeerClient(
            @Value("${aircraft.maintenance.peers:}") String peersConfig,
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

    public AircraftViewDTO findAircraftOnPeers(
            String registrationNumber) {

        boolean unavailablePeer = false;

        for (String peer : peers) {

            for (int attempt = 1; attempt <= 2; attempt++) {

                try {

                    AircraftViewDTO aircraft = restClient.get()
                            .uri(
                                    peer + "/api/aircraft-models/instances/local/{registrationNumber}",
                                    registrationNumber
                            )
                            .retrieve()
                            .body(AircraftViewDTO.class);

                    if (aircraft != null) {

                        System.out.println(
                                "Aircraft " + registrationNumber
                                        + " found on peer " + peer
                        );

                        return aircraft;
                    }

                    unavailablePeer = true;
                    break;

                } catch (RestClientResponseException e) {

                    if (e.getStatusCode().value() == 404) {
                        System.out.println(
                                "Aircraft " + registrationNumber
                                        + " not found on peer " + peer
                        );
                        break;
                    }

                    unavailablePeer = true;

                    System.err.println(
                            "Aircraft peer " + peer
                                    + " failed (attempt " + attempt
                                    + "): HTTP " + e.getStatusCode()
                    );

                } catch (RestClientException e) {

                    unavailablePeer = true;

                    System.err.println(
                            "Aircraft peer " + peer
                                    + " failed (attempt " + attempt
                                    + "): " + e.getMessage()
                    );
                }
            }
        }

        if (unavailablePeer) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Unable to complete aircraft lookup: peer unavailable"
            );
        }

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Aircraft not found: " + registrationNumber
        );
    }
}

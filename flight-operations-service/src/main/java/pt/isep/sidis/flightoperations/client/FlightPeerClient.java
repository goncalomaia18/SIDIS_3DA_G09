package pt.isep.sidis.flightoperations.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import pt.isep.sidis.flightoperations.application.dto.ScheduledFlightViewDTO;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class FlightPeerClient {

    private final RestClient restClient;
    private final List<String> peers;

    public FlightPeerClient(
            @Value("${flight.peers:}") String peersConfig,
            @Value("${flight.peer.username:user}") String username,
            @Value("${flight.peer.password}") String password) {

        this.restClient = RestClient.builder()
                .requestFactory(new org.springframework.http.client
                        .SimpleClientHttpRequestFactory() {{
                    setConnectTimeout(Duration.ofSeconds(2));
                    setReadTimeout(Duration.ofSeconds(3));
                }})
                .defaultHeaders(headers -> headers.setBasicAuth(username, password))
                .build();

        this.peers = new ArrayList<>();

        if (peersConfig != null && !peersConfig.isBlank()) {
            for (String peer : peersConfig.split(",")) {
                if (!peer.isBlank()) {
                    this.peers.add(peer.trim().replaceAll("/+$", ""));
                }
            }
        }
    }

    public List<ScheduledFlightViewDTO> findFlightsOnPeers(String registrationNumber) {

        for (String peer : peers) {
            for (int attempt = 1; attempt <= 2; attempt++) {
                try {
                    ScheduledFlightViewDTO[] result = restClient.get()
                            .uri(peer + "/api/scheduled-flights/local/aircraft/{registration}",
                                    registrationNumber)
                            .retrieve()
                            .onStatus(HttpStatusCode::isError, (request, response) -> {
                                throw new IllegalStateException(
                                        "Peer returned HTTP " + response.getStatusCode());
                            })
                            .body(ScheduledFlightViewDTO[].class);

                    if (result != null && result.length > 0) {
                        return List.of(result);
                    }

                    break;

                } catch (RestClientException | IllegalStateException ex) {
                    System.err.println("Peer " + peer
                            + " failed (attempt " + attempt + "): " + ex.getMessage());
                }
            }
        }

        return List.of();
    }
}

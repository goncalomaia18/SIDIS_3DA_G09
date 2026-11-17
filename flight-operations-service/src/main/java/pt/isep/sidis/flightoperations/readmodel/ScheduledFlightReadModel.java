
package pt.isep.sidis.flightoperations.readmodel;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_flight_read_model")
public class ScheduledFlightReadModel {

    @Id
    private Long id;

    private String routeId;
    private String aircraftRegistrationNumber;
    private LocalDateTime scheduledDateTime;
    private String status;

    protected ScheduledFlightReadModel() {
    }

    public ScheduledFlightReadModel(
            Long id,
            String routeId,
            String aircraftRegistrationNumber,
            LocalDateTime scheduledDateTime,
            String status) {

        this.id = id;
        this.routeId = routeId;
        this.aircraftRegistrationNumber = aircraftRegistrationNumber;
        this.scheduledDateTime = scheduledDateTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getAircraftRegistrationNumber() {
        return aircraftRegistrationNumber;
    }

    public LocalDateTime getScheduledDateTime() {
        return scheduledDateTime;
    }

    public String getStatus() {
        return status;
    }
}

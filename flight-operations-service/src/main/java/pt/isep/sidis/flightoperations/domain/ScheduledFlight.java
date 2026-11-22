
package pt.isep.sidis.flightoperations.domain;

import jakarta.persistence.*;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class ScheduledFlight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID flightUuid;

    @Version
    private Long version;

    @Column(nullable = false)
    private LocalDateTime scheduledDateTime;

    private LocalDateTime actualDepartureDateTime;

    private LocalDateTime actualArrivalDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FlightStatus status;

    @Column(nullable = false)
    private String routeId;

    @Column(nullable = false)
    private String aircraftRegistrationNumber;

    protected ScheduledFlight() {
    }

    public ScheduledFlight(
            String routeId,
            String aircraftRegistrationNumber,
            LocalDateTime scheduledDateTime) {

        Assert.hasText(
                routeId,
                "A route is required to schedule a flight."
        );

        Assert.hasText(
                aircraftRegistrationNumber,
                "An aircraft is required to schedule a flight."
        );

        Assert.notNull(
                scheduledDateTime,
                "The scheduled date and time are required."
        );

        Assert.isTrue(
                scheduledDateTime.isAfter(LocalDateTime.now()),
                "Cannot schedule a flight in the past."
        );

        this.flightUuid = UUID.randomUUID();

        this.routeId = routeId.trim();

        this.aircraftRegistrationNumber =
                aircraftRegistrationNumber.trim().toUpperCase();

        this.scheduledDateTime = scheduledDateTime;
        this.status = FlightStatus.SCHEDULED;
    }

    @PrePersist
    private void ensureFlightUuid() {
        if (flightUuid == null) {
            flightUuid = UUID.randomUUID();
        }
    }

    public void updateStatus(FlightStatus newStatus) {
        Assert.notNull(
                newStatus,
                "Flight status cannot be null."
        );

        this.status = newStatus;
    }

    public void changeAircraft(String newRegistrationNumber) {
        Assert.hasText(
                newRegistrationNumber,
                "Replacement aircraft cannot be null or empty."
        );

        this.aircraftRegistrationNumber =
                newRegistrationNumber.trim().toUpperCase();
    }

    public Long getPk() {
        return pk;
    }

    public UUID getFlightUuid() {
        return flightUuid;
    }

    public Long getVersion() {
        return version;
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

    public LocalDateTime getActualDepartureDateTime() {
        return actualDepartureDateTime;
    }

    public LocalDateTime getActualArrivalDateTime() {
        return actualArrivalDateTime;
    }

    public FlightStatus getStatus() {
        return status;
    }
}

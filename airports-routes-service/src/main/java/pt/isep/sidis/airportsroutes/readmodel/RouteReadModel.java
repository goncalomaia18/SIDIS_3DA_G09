package pt.isep.sidis.airportsroutes.readmodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "route_read_model")
public class RouteReadModel {

    @Id
    @Column(nullable = false)
    private String routeId;

    private String originIata;
    private String destinationIata;
    private Integer estimatedFlightTime;
    private Double minimumRange;
    private Integer minimumCapacity;
    private String status;

    protected RouteReadModel() {}

    public RouteReadModel(String routeId,
                          String originIata,
                          String destinationIata,
                          Integer estimatedFlightTime,
                          Double minimumRange,
                          Integer minimumCapacity,
                          String status) {
        this.routeId = routeId;
        this.originIata = originIata;
        this.destinationIata = destinationIata;
        this.estimatedFlightTime = estimatedFlightTime;
        this.minimumRange = minimumRange;
        this.minimumCapacity = minimumCapacity;
        this.status = status;
    }

    public String getRouteId() { return routeId; }
    public String getOriginIata() { return originIata; }
    public String getDestinationIata() { return destinationIata; }
    public Integer getEstimatedFlightTime() { return estimatedFlightTime; }
    public Double getMinimumRange() { return minimumRange; }
    public Integer getMinimumCapacity() { return minimumCapacity; }
    public String getStatus() { return status; }

    public void setOriginIata(String originIata) { this.originIata = originIata; }
    public void setDestinationIata(String destinationIata) { this.destinationIata = destinationIata; }
    public void setEstimatedFlightTime(Integer estimatedFlightTime) { this.estimatedFlightTime = estimatedFlightTime; }
    public void setMinimumRange(Double minimumRange) { this.minimumRange = minimumRange; }
    public void setMinimumCapacity(Integer minimumCapacity) { this.minimumCapacity = minimumCapacity; }
    public void setStatus(String status) { this.status = status; }
}

package pt.isep.sidis.aircraftmaintenance.readmodel;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "aircraft_read_model")
public class AircraftReadModel {

    @Id
    private String registrationNumber;

    private String modelName;
    private String status;
    private LocalDate manufacturingDate;
    private Integer seatingCapacity;
    private String modelPhotoUrl;
    private String features;
    private String engineType;

    protected AircraftReadModel() {
    }

    public AircraftReadModel(
            String registrationNumber,
            String modelName,
            String status,
            LocalDate manufacturingDate,
            Integer seatingCapacity,
            String modelPhotoUrl,
            String features,
            String engineType) {

        this.registrationNumber = registrationNumber;
        this.modelName = modelName;
        this.status = status;
        this.manufacturingDate = manufacturingDate;
        this.seatingCapacity = seatingCapacity;
        this.modelPhotoUrl = modelPhotoUrl;
        this.features = features;
        this.engineType = engineType;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getModelName() {
        return modelName;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getManufacturingDate() {
        return manufacturingDate;
    }

    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public String getModelPhotoUrl() {
        return modelPhotoUrl;
    }

    public String getFeatures() {
        return features;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

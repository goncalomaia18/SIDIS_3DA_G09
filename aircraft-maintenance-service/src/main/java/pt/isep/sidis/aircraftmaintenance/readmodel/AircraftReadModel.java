package pt.isep.sidis.aircraftmaintenance.readmodel;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "aircraft_read_model")
public class AircraftReadModel {

    @Id
    private String registrationNumber;

    private String modelName;
    private String status;

    protected AircraftReadModel() {
    }

    public AircraftReadModel(String registrationNumber, String modelName, String status) {
        this.registrationNumber = registrationNumber;
        this.modelName = modelName;
        this.status = status;
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

    public void setStatus(String status) {
        this.status = status;
    }
}


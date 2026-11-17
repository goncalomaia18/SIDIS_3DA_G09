package pt.isep.sidis.aircraftmaintenance.readmodel;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "maintenance_record_read_model")
public class MaintenanceRecordReadModel {

    @Id
    private Long maintenanceRecordId;

    private String registrationNumber;
    private String aircraftModel;
    private String status;

    protected MaintenanceRecordReadModel() {
    }

    public MaintenanceRecordReadModel(Long maintenanceRecordId, String registrationNumber, String aircraftModel, String status) {
        this.maintenanceRecordId = maintenanceRecordId;
        this.registrationNumber = registrationNumber;
        this.aircraftModel = aircraftModel;
        this.status = status;
    }

    public Long getMaintenanceRecordId() {
        return maintenanceRecordId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getAircraftModel() {
        return aircraftModel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


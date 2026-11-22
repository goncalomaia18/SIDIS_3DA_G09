
package pt.isep.sidis.aircraftmaintenance.readmodel;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_record_read_model")
public class MaintenanceRecordReadModel {

    @Id
    private Long maintenanceRecordId;

    private String registrationNumber;
    private String aircraftModel;
    private String status;

    private String description;
    private LocalDateTime startDate;
    private Integer expectedDuration;
    private String completionNotes;
    private Double cost;
    private LocalDateTime completionDate;
    private String componentCategory;
    private Long templateId;

    protected MaintenanceRecordReadModel() {
    }

    public MaintenanceRecordReadModel(
            Long maintenanceRecordId,
            String registrationNumber,
            String aircraftModel,
            String status,
            String description,
            LocalDateTime startDate,
            Integer expectedDuration,
            String completionNotes,
            Double cost,
            LocalDateTime completionDate,
            String componentCategory,
            Long templateId) {

        this.maintenanceRecordId = maintenanceRecordId;
        this.registrationNumber = registrationNumber;
        this.aircraftModel = aircraftModel;
        this.status = status;
        this.description = description;
        this.startDate = startDate;
        this.expectedDuration = expectedDuration;
        this.completionNotes = completionNotes;
        this.cost = cost;
        this.completionDate = completionDate;
        this.componentCategory = componentCategory;
        this.templateId = templateId;
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

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public Integer getExpectedDuration() {
        return expectedDuration;
    }

    public String getCompletionNotes() {
        return completionNotes;
    }

    public Double getCost() {
        return cost;
    }

    public LocalDateTime getCompletionDate() {
        return completionDate;
    }

    public String getComponentCategory() {
        return componentCategory;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCompletionNotes(String completionNotes) {
        this.completionNotes = completionNotes;
    }

    public void setCompletionDate(LocalDateTime completionDate) {
        this.completionDate = completionDate;
    }
}

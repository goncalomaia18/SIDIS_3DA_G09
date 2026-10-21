
package pt.isep.sidis.airportsroutes.domain;

import jakarta.persistence.*;
import org.springframework.util.Assert;
import java.time.LocalDate;

@Entity
public class AirplaneCertification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @Column(nullable = false)
    private String certificationNumber;

    // Referência ao modelo existente no Aircraft & Maintenance Service.
    // Não é uma relação JPA com outra base de dados.
    @Column(nullable = false)
    private String aircraftModelName;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    protected AirplaneCertification() {
    }

    public AirplaneCertification(
            String certificationNumber,
            String aircraftModelName,
            LocalDate issueDate,
            LocalDate expiryDate) {

        Assert.hasText(certificationNumber,
                "Certification number must not be blank.");
        Assert.hasText(aircraftModelName,
                "Aircraft model name must not be blank.");
        Assert.notNull(issueDate,
                "Issue date must not be null.");
        Assert.notNull(expiryDate,
                "Expiry date must not be null.");
        Assert.isTrue(expiryDate.isAfter(issueDate),
                "Expiry date must be after the issue date.");

        this.certificationNumber = certificationNumber;
        this.aircraftModelName = aircraftModelName;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
    }

    public String getCertificationNumber() {
        return certificationNumber;
    }

    public String getAircraftModelName() {
        return aircraftModelName;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }
}

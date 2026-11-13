package pt.isep.sidis.airportsroutes.readmodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "airport_read_model")
public class AirportReadModel {

    @Id
    @Column(length = 3, nullable = false)
    private String iataCode;

    private String name;
    private String city;
    private String country;
    private String status;

    protected AirportReadModel() {}

    public AirportReadModel(String iataCode, String name, String city, String country, String status) {
        this.iataCode = iataCode;
        this.name = name;
        this.city = city;
        this.country = country;
        this.status = status;
    }

    public String getIataCode() { return iataCode; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public String getStatus() { return status; }

    public void setName(String name) { this.name = name; }
    public void setCity(String city) { this.city = city; }
    public void setCountry(String country) { this.country = country; }
    public void setStatus(String status) { this.status = status; }
}
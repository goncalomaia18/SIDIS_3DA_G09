package pt.isep.sidis.airportsroutes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.isep.sidis.airportsroutes.readmodel.AirportReadModel;

import java.util.List;

@Repository
public interface AirportReadRepository extends JpaRepository<AirportReadModel, String> {
    List<AirportReadModel> findByStatus(String status);
}
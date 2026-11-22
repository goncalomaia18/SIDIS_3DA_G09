
package pt.isep.sidis.flightoperations.application.projections;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pt.isep.sidis.flightoperations.domain.ScheduledFlight;
import pt.isep.sidis.flightoperations.readmodel.ScheduledFlightReadModel;
import pt.isep.sidis.flightoperations.repositories.ScheduledFlightReadRepository;
import pt.isep.sidis.flightoperations.repositories.ScheduledFlightRepository;

@Component
public class ScheduledFlightReadModelInitializer implements ApplicationRunner {

    private final ScheduledFlightRepository writeRepository;
    private final ScheduledFlightReadRepository readRepository;

    public ScheduledFlightReadModelInitializer(
            ScheduledFlightRepository writeRepository,
            ScheduledFlightReadRepository readRepository) {

        this.writeRepository = writeRepository;
        this.readRepository = readRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        int synchronizedFlights = 0;

        for (ScheduledFlight flight : writeRepository.findAll()) {

            if (readRepository.existsById(flight.getFlightUuid())) {
                continue;
            }

            ScheduledFlightReadModel readModel =
                    new ScheduledFlightReadModel(
                            flight.getFlightUuid(),
                            flight.getRouteId(),
                            flight.getAircraftRegistrationNumber(),
                            flight.getScheduledDateTime(),
                            flight.getStatus().name()
                    );

            readRepository.save(readModel);
            synchronizedFlights++;
        }

        System.out.println(
                "CQRS - READ MODEL INITIALIZED: "
                        + synchronizedFlights
                        + " missing flights synchronized"
        );
    }
}

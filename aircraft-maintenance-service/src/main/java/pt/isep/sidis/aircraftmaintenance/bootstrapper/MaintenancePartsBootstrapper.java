package pt.isep.sidis.aircraftmaintenance.bootstrapper;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pt.isep.sidis.aircraftmaintenance.domain.ComponentCategory;
import pt.isep.sidis.aircraftmaintenance.domain.MaintenancePart;
import pt.isep.sidis.aircraftmaintenance.domain.PartNumber;
import pt.isep.sidis.aircraftmaintenance.repositories.MaintenancePartRepository;

@Order(5)
@Component
public class MaintenancePartsBootstrapper implements CommandLineRunner {

    private final MaintenancePartRepository partRepository;

    public MaintenancePartsBootstrapper(MaintenancePartRepository partRepository) {
        this.partRepository = partRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (partRepository.count() == 0) {
            System.out.println("BOOTSTRAP: Initializing maintenance parts...");

            PartNumber pn1 = new PartNumber("PN-001");

            partRepository.save(new MaintenancePart(
                    pn1,
                    "Wing-Bolt",
                    "Standard bolt for wing attachment",
                    50,
                    10,
                    ComponentCategory.AIRFRAME
            ));

            // se não for para dar alerta, comentar estas duas linhas !!!
            PartNumber pn2 = new PartNumber("PN-002");
            partRepository.save(new MaintenancePart(pn2, "Motor-X", "Engine component", 1, 10, ComponentCategory.ENGINE));
        }
    }
}
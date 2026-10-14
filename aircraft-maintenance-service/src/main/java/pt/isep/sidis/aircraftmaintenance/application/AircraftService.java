
package pt.isep.sidis.aircraftmaintenance.application;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import pt.isep.sidis.aircraftmaintenance.application.dto.*;
import pt.isep.sidis.aircraftmaintenance.domain.*;
import pt.isep.sidis.aircraftmaintenance.repositories.AircraftModelRepository;
import pt.isep.sidis.aircraftmaintenance.repositories.AircraftRepository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class AircraftService {

    private final AircraftModelRepository modelRepository;
    private final AircraftRepository aircraftRepository;

    public AircraftService(AircraftModelRepository modelRepository,
                           AircraftRepository aircraftRepository) {
        this.modelRepository = modelRepository;
        this.aircraftRepository = aircraftRepository;
    }

    private String saveImageLocally(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Path uploadPath = Paths.get("uploads/");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = System.currentTimeMillis()
                    + "_" + file.getOriginalFilename();

            Files.copy(
                    file.getInputStream(),
                    uploadPath.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING
            );

            return "/uploads/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Error saving image " + e.getMessage(), e
            );
        }
    }

    @Transactional
    public void registerModel(RegisterAircraftModelDTO dto) {
        AircraftModel model = new AircraftModel(
                new ModelName(dto.modelName()),
                Manufacturer.valueOf(dto.manufacturer().toUpperCase()),
                dto.seatingCapacity(),
                dto.fuelCapacity(),
                dto.range(),
                dto.speed(),
                dto.fuelConsumption(),
                dto.modelPhotoUrl(),
                dto.seatingConfiguration(),
                dto.operatingHoursRange(),
                dto.engineType()
        );

        modelRepository.save(model);
    }

    @Transactional
    public void registerAircraft(RegisterAircraftDTO dto) {
        AircraftModel model = modelRepository
                .findByModelName(new ModelName(dto.modelName()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aircraft model not found: " + dto.modelName()
                ));

        Aircraft aircraft = new Aircraft(
                new RegistrationNumber(dto.registrationNumber()),
                model,
                dto.manufacturingDate(),
                dto.seatingCapacity(),
                dto.features()
        );

        aircraftRepository.save(aircraft);
    }

    public AircraftViewDTO getAircraftByRegistrationNumber(String regNum) {
        Aircraft aircraft = aircraftRepository
                .findByRegistrationNumber(new RegistrationNumber(regNum))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aircraft not found: " + regNum
                ));

        return mapToViewDTO(aircraft);
    }

    public List<AircraftViewDTO> searchAircrafts(
            String model,
            String status,
            Integer year) {

        List<Aircraft> results;

        if (year != null) {
            results = aircraftRepository.findByManufacturingYear(year);

        } else if (status != null && !status.isBlank()) {
            results = aircraftRepository.findByStatus(
                    AircraftStatus.valueOf(status.toUpperCase())
            );

        } else if (model != null && !model.isBlank()) {
            results = aircraftRepository.findByModel_ModelName(
                    new ModelName(model)
            );

        } else {
            results = aircraftRepository.findAll();
        }

        return results.stream()
                .map(this::mapToViewDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> updateAircraftStatusWithReport(
            String regNum,
            UpdateAircraftStatusDTO dto) {

        Aircraft aircraft = aircraftRepository
                .findByRegistrationNumber(new RegistrationNumber(regNum))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aircraft not found: " + regNum
                ));

        AircraftStatus newStatus = AircraftStatus.valueOf(
                dto.status().toUpperCase()
        );

        aircraft.updateStatus(newStatus);
        aircraftRepository.save(aircraft);

        List<String> swapLogs =
                (newStatus == AircraftStatus.UNDER_MAINTENANCE
                        || newStatus == AircraftStatus.INACTIVE)
                        ? handleAircraftSwap(aircraft)
                        : new ArrayList<>();

        Map<String, Object> response = new HashMap<>();

        response.put("aircraft", mapToViewDTO(aircraft));
        response.put("swapReport", swapLogs);

        return response;
    }

    /**
     * A substituição e o cancelamento de voos pertencem
     * ao Flight Operations Service.
     *
     * TODO SIDIS:
     * Implementar comunicação REST autenticada com esse serviço.
     */
    private List<String> handleAircraftSwap(Aircraft groundedAircraft) {
        return List.of(
                "PENDING: Flight Operations Service must process aircraft "
                        + groundedAircraft.getRegistrationNumber().number()
                        + " status change and perform any required swaps/cancellations."
        );
    }

    @Transactional
    public AircraftModelViewDTO updateModelSpecifications(
            String modelName,
            UpdateAircraftModelSpecsDTO dto) {

        AircraftModel model = modelRepository
                .findByModelName(
                        new ModelName(modelName.trim().toUpperCase())
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aircraft model not found: " + modelName
                ));

        model.updateSpecifications(
                dto.seatingCapacity(),
                dto.fuelCapacity(),
                dto.maximumRange(),
                dto.cruisingSpeed(),
                dto.seatingConfiguration(),
                dto.operatingHoursRange()
        );

        AircraftModel savedModel = modelRepository.save(model);

        return new AircraftModelViewDTO(
                savedModel.getModelName().name(),
                savedModel.getManufacturer().name(),
                savedModel.getDefaultSeatingCapacity(),
                savedModel.getFuelCapacity(),
                savedModel.getMaximumRange(),
                savedModel.getCruisingSpeed(),
                savedModel.getModelPhotoUrl(),
                savedModel.getSeatingConfiguration(),
                savedModel.getOperatingHoursRange(),
                savedModel.getEngineType()
        );
    }

    @Transactional
    public AircraftModelViewDTO updateModelImage(
            String modelName,
            MultipartFile file) {

        AircraftModel model = modelRepository
                .findByModelName(
                        new ModelName(modelName.trim().toUpperCase())
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aircraft model not found: " + modelName
                ));

        model.updateImage(saveImageLocally(file));

        AircraftModel savedModel = modelRepository.save(model);

        return new AircraftModelViewDTO(
                savedModel.getModelName().name(),
                savedModel.getManufacturer().name(),
                savedModel.getDefaultSeatingCapacity(),
                savedModel.getFuelCapacity(),
                savedModel.getMaximumRange(),
                savedModel.getCruisingSpeed(),
                savedModel.getModelPhotoUrl(),
                savedModel.getSeatingConfiguration(),
                savedModel.getOperatingHoursRange(),
                savedModel.getEngineType()
        );
    }

    private AircraftViewDTO mapToViewDTO(Aircraft aircraft) {
        return new AircraftViewDTO(
                aircraft.getRegistrationNumber().toString(),
                aircraft.getAircraftModel().getModelName().name(),
                aircraft.getManufacturingDate(),
                aircraft.getActualSeatingCapacity(),
                aircraft.getStatus().toString(),
                aircraft.getAircraftModel().getModelPhotoUrl(),
                aircraft.getFeatures(),
                aircraft.getAircraftModel().getEngineType()
        );
    }

    public OperationalHoursDTO getAircraftOperationalHours(String regNum) {
        Aircraft aircraft = aircraftRepository
                .findByRegistrationNumber(
                        new RegistrationNumber(regNum.trim().toUpperCase())
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aircraft not found: " + regNum
                ));

        return new OperationalHoursDTO(
                aircraft.getRegistrationNumber().number(),
                aircraft.getTotalFlightHours()
        );
    }

    public org.springframework.data.domain.Page<OperationalHoursDTO>
    getAllAircraftOperationalHours(
            org.springframework.data.domain.Pageable pageable) {

        return aircraftRepository.findAll(pageable)
                .map(aircraft -> new OperationalHoursDTO(
                        aircraft.getRegistrationNumber().number(),
                        aircraft.getTotalFlightHours()
                ));
    }

    /**
     * US204 - Top 5 de modelos de aviões.
     *
     * As estatísticas por assignments serão obtidas
     * através do Flight Operations Service.
     */
    public List<TopAircraftModelDTO> getTop5UtilizedModels(String sortBy) {

        if ("assignments".equalsIgnoreCase(sortBy)) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Flight assignment statistics require Flight Operations Service"
            );
        }

        var pageable = org.springframework.data.domain.PageRequest.of(0, 5);

        return aircraftRepository.findTop5ModelsByFlightHours(pageable);
    }

    /**
     * US223 - Top 5 de aviões individuais.
     *
     * As estatísticas por assignments serão obtidas
     * através do Flight Operations Service.
     */
    public List<TopAircraftModelDTO> getTop5UtilizedAircrafts(String sortBy) {

        if (!"hours".equalsIgnoreCase(sortBy)) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Flight assignment statistics require Flight Operations Service"
            );
        }

        var pageable = org.springframework.data.domain.PageRequest.of(0, 5);

        return aircraftRepository.findTop5AircraftByFlightHours(pageable);
    }

    /**
     * US203 - Pesquisa de rotas compatíveis.
     *
     * TODO SIDIS:
     * Implementar comunicação REST com Airports & Routes Service.
     */
    public org.springframework.data.domain.Page<RouteViewDTO>
    getCompatibleRoutesForAircraft(
            String regNum,
            org.springframework.data.domain.Pageable pageable) {

        aircraftRepository
                .findByRegistrationNumber(
                        new RegistrationNumber(regNum.trim().toUpperCase())
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aircraft not found: " + regNum
                ));

        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Compatible routes require integration with Airports & Routes Service"
        );
    }

    public List<AircraftViewDTO> searchAircraftByFeatures(
            String feature,
            String engineType) {

        return aircraftRepository.findByFeaturesAndEngine(
                        (feature == null || feature.trim().isEmpty())
                                ? null : feature,
                        (engineType == null || engineType.trim().isEmpty())
                                ? null : engineType
                )
                .stream()
                .map(this::mapToViewDTO)
                .collect(Collectors.toList());
    }

    /**
     * US205 - Estado operacional em tempo real.
     *
     * TODO SIDIS:
     * Consultar o Flight Operations Service através de REST.
     */
    public String getRealTimeAircraftStatus(String regNum) {

        Aircraft aircraft = aircraftRepository
                .findByRegistrationNumber(
                        new RegistrationNumber(regNum.trim().toUpperCase())
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aircraft not found: " + regNum
                ));

        if (aircraft.getStatus() == AircraftStatus.UNDER_MAINTENANCE) {
            return "under maintenance";
        }

        if (aircraft.getStatus() == AircraftStatus.INACTIVE) {
            return "inactive";
        }

        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Real-time flight status requires integration with Flight Operations Service"
        );
    }
}

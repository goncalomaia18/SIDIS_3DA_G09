
package pt.isep.sidis.airportsroutes.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import pt.isep.sidis.airportsroutes.application.dto.*;
import pt.isep.sidis.airportsroutes.domain.*;
import pt.isep.sidis.airportsroutes.repositories.AirportRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

@Service
public class AirportService {

    private final AirportRepository airportRepository;

    public AirportService(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    @Transactional
    public AirportViewDTO registerAirport(RegisterAirportDTO dto) {

        Airport airport = new Airport(
                new IATACode(dto.iataCode()),
                dto.name(),
                dto.city(),
                dto.country(),
                dto.timezone(),
                AirportType.valueOf(dto.type().toUpperCase()),
                new Coordinates(dto.latitude(), dto.longitude())
        );

        if (dto.runways() != null) {
            dto.runways().forEach(r ->
                    airport.addRunway(
                            new Runway(
                                    r.name(),
                                    r.length(),
                                    r.orientation()
                            )
                    )
            );
        }

        if (dto.imageUrl() != null && !dto.imageUrl().isBlank()) {
            airport.updateImage(dto.imageUrl());
        }

        if (dto.facilities() != null) {
            dto.facilities().forEach(f ->
                    airport.addFacility(
                            new Facility(
                                    f.type(),
                                    f.description()
                            )
                    )
            );
        }

        Airport saved = airportRepository.save(airport);
        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public AirportViewDTO getAirportByIataCode(String iataCode) {

        String cleanCode = iataCode.trim().toUpperCase();

        Airport airport = airportRepository.findByIataCodeString(cleanCode)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Airport not found with IATA Code: " + cleanCode
                        )
                );

        return mapToDTO(airport);
    }

    @Transactional
    public AirportViewDTO addCertification(
            String iataCode,
            AddCertificationDTO dto) {

        Airport airport = airportRepository
                .findByIataCodeString(iataCode.trim().toUpperCase())
                .orElseThrow(() ->
                        new IllegalArgumentException("Airport not found")
                );

        // TODO: Validar a existência do modelo através de REST
        // no Aircraft & Maintenance Service (porta 8081).

        AirplaneCertification certification =
                new AirplaneCertification(
                        dto.certificationNumber(),
                        dto.modelName(),
                        dto.issueDate(),
                        dto.expiryDate()
                );

        airport.addOrUpdateAirplaneCertification(certification);

        Airport saved = airportRepository.save(airport);

        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<AirportViewDTO> searchAirports(
            String name,
            String city,
            String country) {

        List<Airport> airports;

        if (name != null && !name.isBlank()) {
            airports = airportRepository.findByNameContainingIgnoreCase(name);

        } else if (city != null && !city.isBlank()) {
            airports = airportRepository.findByCityIgnoreCase(city);

        } else if (country != null && !country.isBlank()) {
            airports = airportRepository.findByCountryIgnoreCase(country);

        } else {
            airports = airportRepository.findAll();
        }

        return airports.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AirportViewDTO updateAirportStatus(
            String iataCode,
            String status) {

        Airport airport = airportRepository
                .findByIataCodeString(iataCode.trim().toUpperCase())
                .orElseThrow(() ->
                        new IllegalArgumentException("Airport not found")
                );

        AirportStatus newStatus;

        try {
            newStatus = AirportStatus.valueOf(
                    status.trim().toUpperCase()
            );
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid status. Use: OPERATIONAL, CLOSED, or UNDER_MAINTENANCE"
            );
        }

        airport.updateStatus(newStatus);

        Airport saved = airportRepository.save(airport);

        return mapToDTO(saved);
    }

    @Transactional
    public AirportViewDTO updateAirportImage(
            String iataCode,
            MultipartFile file) {

        Airport airport = airportRepository
                .findByIataCodeString(iataCode.trim().toUpperCase())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Airport not found: " + iataCode
                        )
                );

        try {
            String projectDirectory = System.getProperty("user.dir");

            Path uploadPath = Paths.get(
                    projectDirectory,
                    "uploads"
            );

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String safeFilename = originalFilename == null
                    ? "image"
                    : Paths.get(originalFilename.replace("\\", "/"))
                    .getFileName().toString();

            String fileName =
                    iataCode.trim().toUpperCase() + "_" + safeFilename;

            Path filePath = uploadPath.resolve(fileName);

            Files.copy(
                    file.getInputStream(),
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            String fileUrl = "/uploads/" + fileName;

            airport.updateImage(fileUrl);

            Airport saved = airportRepository.save(airport);

            return mapToDTO(saved);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to save airport image: " + e.getMessage(),
                    e
            );
        }
    }

    private AirportViewDTO mapToDTO(Airport airport) {

        return new AirportViewDTO(
                airport.getIataCode().code(),
                airport.getName(),
                airport.getCity(),
                airport.getCountry(),
                airport.getTimezone(),
                airport.getType().toString(),
                airport.getStatus().toString(),

                airport.getRunways().stream()
                        .map(r -> new RunwayDTO(
                                r.getName(),
                                r.getLength(),
                                r.getOrientation()
                        ))
                        .collect(Collectors.toList()),

                airport.getCertifications().stream()
                        .map(c -> new CertificationViewDTO(
                                c.getCertificationNumber(),
                                c.getAircraftModelName(),
                                c.getExpiryDate()
                        ))
                        .collect(Collectors.toList()),

                airport.getFacilities().stream()
                        .map(f -> new FacilityDTO(
                                f.getType(),
                                f.getDescription()
                        ))
                        .collect(Collectors.toList()),

                airport.getImageUrl(),
                airport.getOperationalHours(),
                airport.getContactInformation()
        );
    }

    @Transactional
    public AirportViewDTO updateAirportDetails(
            String iataCode,
            UpdateAirportDetailsDTO dto) {

        Airport airport = airportRepository
                .findByIataCodeString(iataCode.trim().toUpperCase())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Airport not found: " + iataCode
                        )
                );

        airport.updateDetails(
                dto.operationalHours(),
                dto.contactInformation()
        );

        Airport saved = airportRepository.save(airport);

        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public Object getAirportsGrouped(String groupBy) {

        List<Airport> airports = airportRepository.findAll();

        if ("both".equalsIgnoreCase(groupBy)) {
            return airports.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.groupingBy(
                            this::extractRegion,
                            Collectors.groupingBy(dto ->
                                    dto.country() != null
                                            ? dto.country()
                                            : "Unknown Country"
                            )
                    ));
        }

        if ("region".equalsIgnoreCase(groupBy)) {
            return airports.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.groupingBy(
                            this::extractRegion
                    ));
        }

        return airports.stream()
                .map(this::mapToDTO)
                .collect(Collectors.groupingBy(dto ->
                        dto.country() != null
                                ? dto.country()
                                : "Unknown Country"
                ));
    }

    private String extractRegion(AirportViewDTO dto) {

        if (dto.timezone() != null
                && dto.timezone().contains("/")) {

            return dto.timezone().split("/")[0];
        }

        return "Unknown Region";
    }

    @Transactional
    public List<AirportViewDTO> importAirportsFromCsv(
            MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException(
                    "The uploaded CSV file is empty."
            );
        }

        List<AirportViewDTO> importedAirports = new ArrayList<>();

        try (
                BufferedReader fileReader = new BufferedReader(
                        new InputStreamReader(
                                file.getInputStream(),
                                java.nio.charset.StandardCharsets.UTF_8
                        )
                );

                CSVParser csvParser = new CSVParser(
                        fileReader,
                        CSVFormat.DEFAULT
                                .withFirstRecordAsHeader()
                                .withIgnoreHeaderCase()
                                .withTrim()
                )
        ) {

            for (CSVRecord csvRecord : csvParser) {

                RegisterAirportDTO dto = new RegisterAirportDTO(
                        csvRecord.get("iataCode"),
                        csvRecord.get("name"),
                        csvRecord.get("city"),
                        csvRecord.get("country"),
                        csvRecord.get("timezone"),
                        csvRecord.get("type"),
                        Double.parseDouble(
                                csvRecord.get("latitude")
                        ),
                        Double.parseDouble(
                                csvRecord.get("longitude")
                        ),
                        List.of(),
                        List.of(),
                        null
                );

                AirportViewDTO savedAirport = registerAirport(dto);

                importedAirports.add(savedAirport);
            }

            return importedAirports;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Error parsing the CSV file: " + e.getMessage(),
                    e
            );

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Error in CSV formatting/data: " + e.getMessage(),
                    e
            );
        }
    }
}


package pt.isep.sidis.airportsroutes.api;

import jakarta.validation.Valid;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import pt.isep.sidis.airportsroutes.application.AirportService;
import pt.isep.sidis.airportsroutes.application.dto.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    private final AirportService service;

    public AirportController(AirportService service) {
        this.service = service;
    }

    // US - Register a new airport
    @PostMapping
    public ResponseEntity<EntityModel<AirportViewDTO>> createAirport(
            @Valid @RequestBody RegisterAirportDTO dto) {

        AirportViewDTO created = service.registerAirport(dto);

        EntityModel<AirportViewDTO> resource = EntityModel.of(created);

        resource.add(
                linkTo(methodOn(AirportController.class)
                        .getAirport(created.iataCode()))
                        .withSelfRel()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    // Get airport by IATA code
    @GetMapping("/{iataCode}")
    public ResponseEntity<EntityModel<AirportViewDTO>> getAirport(
            @PathVariable String iataCode) {

        AirportViewDTO airport = service.getAirportByIataCode(iataCode);

        EntityModel<AirportViewDTO> resource = EntityModel.of(airport);

        resource.add(
                linkTo(methodOn(AirportController.class)
                        .getAirport(iataCode))
                        .withSelfRel()
        );

        resource.add(
                linkTo(methodOn(AirportController.class)
                        .addCertification(iataCode, null))
                        .withRel("add-certification")
        );

        return ResponseEntity.ok(resource);
    }

    // Add or update aircraft certification
    @PostMapping("/{iataCode}/certifications")
    public ResponseEntity<EntityModel<AirportViewDTO>> addCertification(
            @PathVariable String iataCode,
            @Valid @RequestBody AddCertificationDTO dto) {

        AirportViewDTO updatedAirport =
                service.addCertification(iataCode, dto);

        EntityModel<AirportViewDTO> resource =
                EntityModel.of(updatedAirport);

        resource.add(
                linkTo(methodOn(AirportController.class)
                        .getAirport(iataCode))
                        .withRel("airport-details")
        );

        resource.add(
                linkTo(methodOn(AirportController.class)
                        .addCertification(iataCode, dto))
                        .withSelfRel()
        );

        return ResponseEntity.ok(resource);
    }

    // Search airports by name, city or country
    @GetMapping("/search")
    public ResponseEntity<CollectionModel<EntityModel<AirportViewDTO>>>
    searchAirports(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country) {

        List<AirportViewDTO> airports =
                service.searchAirports(name, city, country);

        List<EntityModel<AirportViewDTO>> resources =
                airports.stream()
                        .map(dto -> EntityModel.of(
                                dto,
                                linkTo(methodOn(AirportController.class)
                                        .getAirport(dto.iataCode()))
                                        .withSelfRel()
                        ))
                        .collect(Collectors.toList());

        CollectionModel<EntityModel<AirportViewDTO>> collection =
                CollectionModel.of(
                        resources,
                        linkTo(methodOn(AirportController.class)
                                .searchAirports(name, city, country))
                                .withSelfRel()
                );

        return ResponseEntity.ok(collection);
    }

    // US109 - Update airport status
    @PatchMapping("/{iataCode}/status")
    public ResponseEntity<EntityModel<AirportViewDTO>> updateStatus(
            @PathVariable String iataCode,
            @RequestBody String newStatus) {

        String cleanStatus = newStatus.replace("\"", "").trim();

        AirportViewDTO updated =
                service.updateAirportStatus(iataCode, cleanStatus);

        EntityModel<AirportViewDTO> resource = EntityModel.of(updated);

        resource.add(
                linkTo(methodOn(AirportController.class)
                        .getAirport(iataCode))
                        .withRel("airport-details")
        );

        return ResponseEntity.ok(resource);
    }

    // US207 - Upload airport image or map
    @PatchMapping(
            value = "/{iataCode}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<EntityModel<AirportViewDTO>> updateAirportImage(
            @PathVariable String iataCode,
            @RequestParam("file") MultipartFile file) {

        AirportViewDTO updatedAirport =
                service.updateAirportImage(iataCode, file);

        EntityModel<AirportViewDTO> resource =
                EntityModel.of(updatedAirport);

        resource.add(
                linkTo(methodOn(AirportController.class)
                        .updateAirportImage(iataCode, null))
                        .withSelfRel()
        );

        resource.add(
                linkTo(methodOn(AirportController.class)
                        .getAirport(iataCode))
                        .withRel("airport-details")
        );

        return ResponseEntity.ok(resource);
    }

    // US208 - Update airport details
    @PatchMapping("/{iataCode}/details")
    public ResponseEntity<EntityModel<AirportViewDTO>> updateAirportDetails(
            @PathVariable String iataCode,
            @Valid @RequestBody UpdateAirportDetailsDTO dto) {

        AirportViewDTO updatedAirport =
                service.updateAirportDetails(iataCode, dto);

        EntityModel<AirportViewDTO> resource =
                EntityModel.of(updatedAirport);

        resource.add(
                linkTo(methodOn(AirportController.class)
                        .getAirport(iataCode))
                        .withRel("airport-details")
        );

        resource.add(
                linkTo(methodOn(AirportController.class)
                        .updateAirportDetails(iataCode, dto))
                        .withSelfRel()
        );

        return ResponseEntity.ok(resource);
    }

    // US211 - Group airports by region, country or both
    @GetMapping("/grouped")
    public ResponseEntity<Object> getAirportsGrouped(
            @RequestParam(defaultValue = "country") String by) {

        Object groupedAirports = service.getAirportsGrouped(by);

        if (groupedAirports instanceof Map<?, ?> map
                && map.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(groupedAirports);
    }

    // US225 - Import airports from CSV
    @PostMapping(
            value = "/import",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<CollectionModel<EntityModel<AirportViewDTO>>>
    importAirportsFromCsv(
            @RequestParam("file") MultipartFile file) {

        List<AirportViewDTO> importedAirports =
                service.importAirportsFromCsv(file);

        List<EntityModel<AirportViewDTO>> resources =
                importedAirports.stream()
                        .map(dto -> EntityModel.of(
                                dto,
                                linkTo(methodOn(AirportController.class)
                                        .getAirport(dto.iataCode()))
                                        .withSelfRel()
                        ))
                        .collect(Collectors.toList());

        CollectionModel<EntityModel<AirportViewDTO>> collection =
                CollectionModel.of(
                        resources,
                        linkTo(methodOn(AirportController.class)
                                .importAirportsFromCsv(null))
                                .withSelfRel()
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(collection);
    }
}

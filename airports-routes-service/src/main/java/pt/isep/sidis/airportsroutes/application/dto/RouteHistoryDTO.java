package pt.isep.sidis.airportsroutes.application.dto;

import java.time.LocalDateTime;

public record RouteHistoryDTO(
        String routeId,
        String action,
        LocalDateTime startDate,
        LocalDateTime endDate
) {}
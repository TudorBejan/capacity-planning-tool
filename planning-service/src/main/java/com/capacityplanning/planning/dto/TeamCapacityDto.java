package com.capacityplanning.planning.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Mirror of team-service's TeamCapacityDto, received via REST. */
public record TeamCapacityDto(
        UUID teamId,
        String teamName,
        LocalDate startDate,
        LocalDate endDate,
        int memberCount,
        BigDecimal totalFte,
        BigDecimal overheadPercentage,
        BigDecimal workingWeeksInPeriod,
        BigDecimal netCapacityWeeks
) {}

package com.capacityplanning.team.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Capacity of a team for a given date range.
 * netCapacityWeeks = sum(person.availability%) * (1 - overhead%) * workingWeeksInPeriod
 */
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

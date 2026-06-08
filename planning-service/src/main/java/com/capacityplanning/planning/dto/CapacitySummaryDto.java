package com.capacityplanning.planning.dto;

import java.time.LocalDate;
import java.util.List;

public record CapacitySummaryDto(
        LocalDate startDate,
        LocalDate endDate,
        List<TeamAllocationDto> teamAllocations
) {}

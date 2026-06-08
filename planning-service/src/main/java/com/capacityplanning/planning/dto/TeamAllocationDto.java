package com.capacityplanning.planning.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TeamAllocationDto(
        UUID teamId,
        String teamName,
        BigDecimal capacityWeeks,
        BigDecimal allocatedWeeks,
        BigDecimal utilizationPercentage
) {}

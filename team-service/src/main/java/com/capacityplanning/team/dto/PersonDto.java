package com.capacityplanning.team.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PersonDto(
        UUID id,
        UUID teamId,
        @NotBlank String name,
        String role,
        @NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal availabilityPercentage,
        LocalDateTime createdAt
) {}

package com.capacityplanning.team.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TeamDto(
        UUID id,
        @NotBlank String name,
        String description,
        @NotNull @DecimalMin("0") @DecimalMax("100") BigDecimal overheadPercentage,
        List<PersonDto> persons,
        LocalDateTime createdAt
) {}

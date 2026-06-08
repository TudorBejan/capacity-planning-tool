package com.capacityplanning.planning.dto;

import com.capacityplanning.planning.model.Epic.EpicStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record EpicDto(
        UUID id,
        UUID initiativeId,
        @NotNull UUID teamId,
        String teamName,
        @NotBlank String name,
        String description,
        @NotNull EpicStatus status,
        @NotNull @DecimalMin("0.1") BigDecimal estimatedWeeks,
        LocalDate startDate,
        LocalDate dueDate,
        LocalDateTime createdAt
) {}

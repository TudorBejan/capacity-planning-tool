package com.capacityplanning.planning.dto;

import com.capacityplanning.planning.model.Initiative.InitiativeStatus;
import com.capacityplanning.planning.model.Initiative.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record InitiativeDto(
        UUID id,
        @NotBlank String name,
        String description,
        @NotNull InitiativeStatus status,
        @NotNull Priority priority,
        LocalDate targetDate,
        List<EpicDto> epics,
        LocalDateTime createdAt
) {}

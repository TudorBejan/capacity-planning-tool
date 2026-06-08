package com.capacityplanning.planning.service;

import com.capacityplanning.planning.client.TeamServiceClient;
import com.capacityplanning.planning.dto.CapacitySummaryDto;
import com.capacityplanning.planning.dto.TeamAllocationDto;
import com.capacityplanning.planning.dto.TeamCapacityDto;
import com.capacityplanning.planning.model.Epic;
import com.capacityplanning.planning.repository.EpicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CapacityService {

    private final EpicRepository epicRepository;
    private final TeamServiceClient teamServiceClient;

    /**
     * For each team that has epics overlapping the date range,
     * computes: capacity (from team-service) vs. total estimated effort.
     */
    public CapacitySummaryDto getSummary(LocalDate startDate, LocalDate endDate) {
        List<Epic> overlapping = epicRepository.findOverlappingEpics(startDate, endDate);

        // Sum allocated weeks per team
        Map<UUID, Double> allocatedByTeam = overlapping.stream()
                .collect(Collectors.groupingBy(
                        Epic::getTeamId,
                        Collectors.summingDouble(e -> e.getEstimatedWeeks().doubleValue())));

        List<TeamAllocationDto> allocations = new ArrayList<>();
        for (Map.Entry<UUID, Double> entry : allocatedByTeam.entrySet()) {
            UUID teamId = entry.getKey();
            double allocated = entry.getValue();

            TeamCapacityDto capacity = teamServiceClient.getTeamCapacity(teamId, startDate, endDate);
            if (capacity == null) continue;

            double cap = capacity.netCapacityWeeks().doubleValue();
            double utilization = cap > 0 ? (allocated / cap * 100.0) : 0.0;

            allocations.add(new TeamAllocationDto(
                    teamId,
                    capacity.teamName(),
                    BigDecimal.valueOf(cap).setScale(2, RoundingMode.HALF_UP),
                    BigDecimal.valueOf(allocated).setScale(2, RoundingMode.HALF_UP),
                    BigDecimal.valueOf(utilization).setScale(1, RoundingMode.HALF_UP)
            ));
        }

        // Sort by utilization desc so most-loaded teams appear first
        allocations.sort(Comparator.comparing(TeamAllocationDto::utilizationPercentage).reversed());

        return new CapacitySummaryDto(startDate, endDate, allocations);
    }
}

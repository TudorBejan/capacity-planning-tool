package com.capacityplanning.team.service;

import com.capacityplanning.team.dto.TeamCapacityDto;
import com.capacityplanning.team.model.Person;
import com.capacityplanning.team.model.Team;
import com.capacityplanning.team.repository.PersonRepository;
import com.capacityplanning.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CapacityService {

    private final TeamRepository teamRepository;
    private final PersonRepository personRepository;

    public TeamCapacityDto calculateCapacity(UUID teamId, LocalDate startDate, LocalDate endDate) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new NoSuchElementException("Team not found: " + teamId));
        List<Person> persons = personRepository.findByTeamId(teamId);

        double workingDays = countWorkingDays(startDate, endDate);
        BigDecimal workingWeeks = BigDecimal.valueOf(workingDays / 5.0)
                .setScale(2, RoundingMode.HALF_UP);

        double totalFteRaw = persons.stream()
                .mapToDouble(p -> p.getAvailabilityPercentage().doubleValue() / 100.0)
                .sum();
        BigDecimal totalFte = BigDecimal.valueOf(totalFteRaw).setScale(2, RoundingMode.HALF_UP);

        // Net capacity after overhead: FTE * (1 - overhead%) * weeks
        double overhead = team.getOverheadPercentage().doubleValue() / 100.0;
        double netWeeks = totalFteRaw * (1.0 - overhead) * workingWeeks.doubleValue();
        BigDecimal netCapacityWeeks = BigDecimal.valueOf(netWeeks).setScale(2, RoundingMode.HALF_UP);

        return new TeamCapacityDto(
                team.getId(),
                team.getName(),
                startDate,
                endDate,
                persons.size(),
                totalFte,
                team.getOverheadPercentage(),
                workingWeeks,
                netCapacityWeeks
        );
    }

    private long countWorkingDays(LocalDate start, LocalDate end) {
        return start.datesUntil(end.plusDays(1))
                .filter(d -> d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY)
                .count();
    }
}

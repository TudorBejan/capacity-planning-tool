package com.capacityplanning.planning.service;

import com.capacityplanning.planning.client.TeamServiceClient;
import com.capacityplanning.planning.dto.CapacitySummaryDto;
import com.capacityplanning.planning.dto.TeamCapacityDto;
import com.capacityplanning.planning.model.Epic;
import com.capacityplanning.planning.model.Initiative;
import com.capacityplanning.planning.repository.EpicRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CapacityServiceTest {

    @Mock EpicRepository epicRepository;
    @Mock TeamServiceClient teamServiceClient;
    @InjectMocks CapacityService capacityService;

    private static final LocalDate START = LocalDate.of(2025, 1, 1);
    private static final LocalDate END   = LocalDate.of(2025, 3, 31);

    private Epic epicForTeam(UUID teamId, double weeks) {
        Initiative i = new Initiative();
        i.setId(UUID.randomUUID());
        Epic e = new Epic();
        e.setId(UUID.randomUUID());
        e.setInitiative(i);
        e.setTeamId(teamId);
        e.setName("Epic");
        e.setStatus(Epic.EpicStatus.IN_PROGRESS);
        e.setEstimatedWeeks(BigDecimal.valueOf(weeks));
        e.setStartDate(START);
        e.setDueDate(END);
        return e;
    }

    private TeamCapacityDto capacity(UUID teamId, String name, double netWeeks) {
        return new TeamCapacityDto(teamId, name, START, END, 3,
                new BigDecimal("3.0"), new BigDecimal("20.0"),
                new BigDecimal("13.0"), BigDecimal.valueOf(netWeeks));
    }

    // ── happy paths ───────────────────────────────────────────────────────────

    @Test
    void getSummary_singleTeam_computesUtilization() {
        UUID teamId = UUID.randomUUID();
        when(epicRepository.findOverlappingEpics(START, END))
                .thenReturn(List.of(epicForTeam(teamId, 8.0)));
        when(teamServiceClient.getTeamCapacity(teamId, START, END))
                .thenReturn(capacity(teamId, "Alpha", 10.0));

        CapacitySummaryDto result = capacityService.getSummary(START, END);

        assertThat(result.startDate()).isEqualTo(START);
        assertThat(result.endDate()).isEqualTo(END);
        assertThat(result.teamAllocations()).hasSize(1);

        var alloc = result.teamAllocations().get(0);
        assertThat(alloc.teamName()).isEqualTo("Alpha");
        assertThat(alloc.allocatedWeeks()).isEqualByComparingTo("8.00");
        assertThat(alloc.capacityWeeks()).isEqualByComparingTo("10.00");
        assertThat(alloc.utilizationPercentage()).isEqualByComparingTo("80.0");
    }

    @Test
    void getSummary_multipleEpicsSameTeam_sumsAllocatedWeeks() {
        UUID teamId = UUID.randomUUID();
        when(epicRepository.findOverlappingEpics(START, END))
                .thenReturn(List.of(epicForTeam(teamId, 4.0), epicForTeam(teamId, 6.0)));
        when(teamServiceClient.getTeamCapacity(teamId, START, END))
                .thenReturn(capacity(teamId, "Beta", 10.0));

        CapacitySummaryDto result = capacityService.getSummary(START, END);

        assertThat(result.teamAllocations()).hasSize(1);
        assertThat(result.teamAllocations().get(0).allocatedWeeks()).isEqualByComparingTo("10.00");
        assertThat(result.teamAllocations().get(0).utilizationPercentage()).isEqualByComparingTo("100.0");
    }

    @Test
    void getSummary_multipleTeams_sortedByUtilizationDescending() {
        UUID lowTeam  = UUID.randomUUID();
        UUID highTeam = UUID.randomUUID();
        when(epicRepository.findOverlappingEpics(START, END))
                .thenReturn(List.of(epicForTeam(lowTeam, 2.0), epicForTeam(highTeam, 9.0)));
        when(teamServiceClient.getTeamCapacity(lowTeam, START, END))
                .thenReturn(capacity(lowTeam, "LowLoad", 10.0));
        when(teamServiceClient.getTeamCapacity(highTeam, START, END))
                .thenReturn(capacity(highTeam, "HighLoad", 10.0));

        CapacitySummaryDto result = capacityService.getSummary(START, END);

        assertThat(result.teamAllocations())
                .extracting("teamName")
                .containsExactly("HighLoad", "LowLoad");
    }

    // ── edge cases ────────────────────────────────────────────────────────────

    @Test
    void getSummary_noOverlappingEpics_returnsEmptyAllocations() {
        when(epicRepository.findOverlappingEpics(START, END)).thenReturn(List.of());

        CapacitySummaryDto result = capacityService.getSummary(START, END);

        assertThat(result.teamAllocations()).isEmpty();
        verifyNoInteractions(teamServiceClient);
    }

    @Test
    void getSummary_teamClientReturnsNull_teamIsExcluded() {
        UUID teamId = UUID.randomUUID();
        when(epicRepository.findOverlappingEpics(START, END))
                .thenReturn(List.of(epicForTeam(teamId, 5.0)));
        when(teamServiceClient.getTeamCapacity(teamId, START, END)).thenReturn(null);

        CapacitySummaryDto result = capacityService.getSummary(START, END);

        assertThat(result.teamAllocations()).isEmpty();
    }

    @Test
    void getSummary_zeroNetCapacity_yieldsZeroUtilization() {
        UUID teamId = UUID.randomUUID();
        when(epicRepository.findOverlappingEpics(START, END))
                .thenReturn(List.of(epicForTeam(teamId, 5.0)));
        when(teamServiceClient.getTeamCapacity(teamId, START, END))
                .thenReturn(capacity(teamId, "Empty", 0.0));

        CapacitySummaryDto result = capacityService.getSummary(START, END);

        assertThat(result.teamAllocations().get(0).utilizationPercentage())
                .isEqualByComparingTo("0.0");
    }

    @Test
    void getSummary_overAllocated_utilizationExceeds100() {
        UUID teamId = UUID.randomUUID();
        when(epicRepository.findOverlappingEpics(START, END))
                .thenReturn(List.of(epicForTeam(teamId, 15.0)));
        when(teamServiceClient.getTeamCapacity(teamId, START, END))
                .thenReturn(capacity(teamId, "Overloaded", 10.0));

        CapacitySummaryDto result = capacityService.getSummary(START, END);

        assertThat(result.teamAllocations().get(0).utilizationPercentage())
                .isGreaterThan(BigDecimal.valueOf(100));
    }
}

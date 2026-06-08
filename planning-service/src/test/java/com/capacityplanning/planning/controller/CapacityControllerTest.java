package com.capacityplanning.planning.controller;

import com.capacityplanning.planning.dto.CapacitySummaryDto;
import com.capacityplanning.planning.dto.TeamAllocationDto;
import com.capacityplanning.planning.service.CapacityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CapacityController.class)
class CapacityControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean CapacityService capacityService;

    private static final LocalDate START = LocalDate.of(2025, 1, 1);
    private static final LocalDate END   = LocalDate.of(2025, 3, 31);

    // ── GET /api/capacity/summary ─────────────────────────────────────────────

    @Test
    void getSummary_returns200WithAllocations() throws Exception {
        UUID teamId = UUID.randomUUID();
        CapacitySummaryDto summary = new CapacitySummaryDto(START, END, List.of(
                new TeamAllocationDto(teamId, "Alpha",
                        new BigDecimal("10.00"), new BigDecimal("8.00"), new BigDecimal("80.0"))
        ));
        when(capacityService.getSummary(START, END)).thenReturn(summary);

        mockMvc.perform(get("/api/capacity/summary")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-03-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value("2025-01-01"))
                .andExpect(jsonPath("$.endDate").value("2025-03-31"))
                .andExpect(jsonPath("$.teamAllocations", hasSize(1)))
                .andExpect(jsonPath("$.teamAllocations[0].teamName").value("Alpha"))
                .andExpect(jsonPath("$.teamAllocations[0].allocatedWeeks").value(8.00))
                .andExpect(jsonPath("$.teamAllocations[0].capacityWeeks").value(10.00))
                .andExpect(jsonPath("$.teamAllocations[0].utilizationPercentage").value(80.0));
    }

    @Test
    void getSummary_noAllocations_returnsEmptyList() throws Exception {
        when(capacityService.getSummary(START, END))
                .thenReturn(new CapacitySummaryDto(START, END, List.of()));

        mockMvc.perform(get("/api/capacity/summary")
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-03-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamAllocations", hasSize(0)));
    }

    @Test
    void getSummary_missingStartDate_returns400() throws Exception {
        mockMvc.perform(get("/api/capacity/summary")
                        .param("endDate", "2025-03-31"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSummary_missingEndDate_returns400() throws Exception {
        mockMvc.perform(get("/api/capacity/summary")
                        .param("startDate", "2025-01-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSummary_missingBothParams_returns400() throws Exception {
        mockMvc.perform(get("/api/capacity/summary"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSummary_invalidDateFormat_returns400() throws Exception {
        mockMvc.perform(get("/api/capacity/summary")
                        .param("startDate", "01-01-2025")
                        .param("endDate", "2025-03-31"))
                .andExpect(status().isBadRequest());
    }
}

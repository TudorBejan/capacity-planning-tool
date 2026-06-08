package com.capacityplanning.planning.controller;

import com.capacityplanning.planning.dto.EpicDto;
import com.capacityplanning.planning.model.Epic.EpicStatus;
import com.capacityplanning.planning.service.EpicService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EpicController.class)
class EpicControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean EpicService epicService;

    private static final UUID EPIC_ID        = UUID.fromString("22222222-0000-0000-0000-000000000001");
    private static final UUID INITIATIVE_ID  = UUID.fromString("11111111-0000-0000-0000-000000000001");
    private static final UUID TEAM_ID        = UUID.fromString("33333333-0000-0000-0000-000000000001");

    private EpicDto dto() {
        return new EpicDto(EPIC_ID, INITIATIVE_ID, TEAM_ID, "Alpha Team",
                "Auth SSO", "OIDC integration", EpicStatus.PLANNED,
                new BigDecimal("4.0"), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31), null);
    }

    private EpicDto requestDto() {
        return new EpicDto(null, INITIATIVE_ID, TEAM_ID, null,
                "Auth SSO", "OIDC integration", EpicStatus.PLANNED,
                new BigDecimal("4.0"), LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31), null);
    }

    // ── GET /api/epics ────────────────────────────────────────────────────────

    @Test
    void getAll_returns200WithList() throws Exception {
        when(epicService.findAll()).thenReturn(List.of(dto()));

        mockMvc.perform(get("/api/epics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Auth SSO"))
                .andExpect(jsonPath("$[0].status").value("PLANNED"));
    }

    // ── GET /api/epics/{id} ───────────────────────────────────────────────────

    @Test
    void getById_found_returns200() throws Exception {
        when(epicService.findById(EPIC_ID)).thenReturn(dto());

        mockMvc.perform(get("/api/epics/{id}", EPIC_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(EPIC_ID.toString()))
                .andExpect(jsonPath("$.teamId").value(TEAM_ID.toString()))
                .andExpect(jsonPath("$.estimatedWeeks").value(4.0));
    }

    @Test
    void getById_missing_returns404() throws Exception {
        when(epicService.findById(EPIC_ID)).thenThrow(new NoSuchElementException("Epic not found: " + EPIC_ID));

        mockMvc.perform(get("/api/epics/{id}", EPIC_ID))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/epics/by-initiative/{id} ────────────────────────────────────

    @Test
    void getByInitiative_returns200WithList() throws Exception {
        when(epicService.findByInitiative(INITIATIVE_ID)).thenReturn(List.of(dto()));

        mockMvc.perform(get("/api/epics/by-initiative/{id}", INITIATIVE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].initiativeId").value(INITIATIVE_ID.toString()));
    }

    // ── POST /api/epics ───────────────────────────────────────────────────────

    @Test
    void create_validBody_returns201() throws Exception {
        when(epicService.create(any())).thenReturn(dto());

        mockMvc.perform(post("/api/epics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(EPIC_ID.toString()));
    }

    @Test
    void create_missingName_returns400() throws Exception {
        String body = """
                {"initiativeId": "%s", "teamId": "%s", "status": "PLANNED", "estimatedWeeks": 4.0}
                """.formatted(INITIATIVE_ID, TEAM_ID);
        mockMvc.perform(post("/api/epics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_estimatedWeeksZero_returns400() throws Exception {
        EpicDto invalid = new EpicDto(null, INITIATIVE_ID, TEAM_ID, null,
                "Epic", null, EpicStatus.PLANNED, BigDecimal.ZERO, null, null, null);

        mockMvc.perform(post("/api/epics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_missingTeamId_returns400() throws Exception {
        String body = """
                {"initiativeId": "%s", "name": "X", "status": "PLANNED", "estimatedWeeks": 2.0}
                """.formatted(INITIATIVE_ID);
        mockMvc.perform(post("/api/epics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ── PUT /api/epics/{id} ───────────────────────────────────────────────────

    @Test
    void update_validBody_returns200() throws Exception {
        EpicDto updated = new EpicDto(EPIC_ID, INITIATIVE_ID, TEAM_ID, null,
                "Updated", null, EpicStatus.IN_PROGRESS, new BigDecimal("6.0"), null, null, null);
        when(epicService.update(eq(EPIC_ID), any())).thenReturn(updated);

        mockMvc.perform(put("/api/epics/{id}", EPIC_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    // ── DELETE /api/epics/{id} ────────────────────────────────────────────────

    @Test
    void delete_existing_returns204() throws Exception {
        doNothing().when(epicService).delete(EPIC_ID);

        mockMvc.perform(delete("/api/epics/{id}", EPIC_ID))
                .andExpect(status().isNoContent());
        verify(epicService).delete(EPIC_ID);
    }

    @Test
    void delete_missing_returns404() throws Exception {
        doThrow(new NoSuchElementException("Epic not found: " + EPIC_ID))
                .when(epicService).delete(EPIC_ID);

        mockMvc.perform(delete("/api/epics/{id}", EPIC_ID))
                .andExpect(status().isNotFound());
    }
}

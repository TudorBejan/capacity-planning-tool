package com.capacityplanning.planning.controller;

import com.capacityplanning.planning.dto.InitiativeDto;
import com.capacityplanning.planning.model.Initiative.InitiativeStatus;
import com.capacityplanning.planning.model.Initiative.Priority;
import com.capacityplanning.planning.service.InitiativeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

@WebMvcTest(InitiativeController.class)
class InitiativeControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean InitiativeService initiativeService;

    private static final UUID ID = UUID.fromString("11111111-0000-0000-0000-000000000001");

    private InitiativeDto dto() {
        return new InitiativeDto(ID, "Portal Redesign", "Modern UX",
                InitiativeStatus.ACTIVE, Priority.HIGH,
                LocalDate.of(2025, 12, 31), List.of(), null);
    }

    private InitiativeDto requestDto() {
        return new InitiativeDto(null, "Portal Redesign", "Modern UX",
                InitiativeStatus.ACTIVE, Priority.HIGH,
                LocalDate.of(2025, 12, 31), null, null);
    }

    // ── GET /api/initiatives ──────────────────────────────────────────────────

    @Test
    void getAll_returns200WithList() throws Exception {
        when(initiativeService.findAll()).thenReturn(List.of(dto()));

        mockMvc.perform(get("/api/initiatives"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Portal Redesign"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    // ── GET /api/initiatives/{id} ─────────────────────────────────────────────

    @Test
    void getById_found_returns200() throws Exception {
        when(initiativeService.findById(ID)).thenReturn(dto());

        mockMvc.perform(get("/api/initiatives/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID.toString()))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void getById_missing_returns404() throws Exception {
        when(initiativeService.findById(ID)).thenThrow(new NoSuchElementException("Initiative not found: " + ID));

        mockMvc.perform(get("/api/initiatives/{id}", ID))
                .andExpect(status().isNotFound());
    }

    // ── POST /api/initiatives ─────────────────────────────────────────────────

    @Test
    void create_validBody_returns201WithDto() throws Exception {
        when(initiativeService.create(any())).thenReturn(dto());

        mockMvc.perform(post("/api/initiatives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ID.toString()))
                .andExpect(jsonPath("$.name").value("Portal Redesign"));
    }

    @Test
    void create_missingName_returns400() throws Exception {
        String body = """
                {"status": "ACTIVE", "priority": "HIGH"}
                """;
        mockMvc.perform(post("/api/initiatives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_missingStatus_returns400() throws Exception {
        String body = """
                {"name": "X", "priority": "HIGH"}
                """;
        mockMvc.perform(post("/api/initiatives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_missingPriority_returns400() throws Exception {
        String body = """
                {"name": "X", "status": "ACTIVE"}
                """;
        mockMvc.perform(post("/api/initiatives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ── PUT /api/initiatives/{id} ─────────────────────────────────────────────

    @Test
    void update_validBody_returns200() throws Exception {
        InitiativeDto updated = new InitiativeDto(ID, "Updated", null,
                InitiativeStatus.COMPLETED, Priority.LOW, null, List.of(), null);
        when(initiativeService.update(eq(ID), any())).thenReturn(updated);

        mockMvc.perform(put("/api/initiatives/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void update_missing_returns404() throws Exception {
        when(initiativeService.update(eq(ID), any()))
                .thenThrow(new NoSuchElementException("Initiative not found: " + ID));

        mockMvc.perform(put("/api/initiatives/{id}", ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto())))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/initiatives/{id} ──────────────────────────────────────────

    @Test
    void delete_existing_returns204() throws Exception {
        doNothing().when(initiativeService).delete(ID);

        mockMvc.perform(delete("/api/initiatives/{id}", ID))
                .andExpect(status().isNoContent());
        verify(initiativeService).delete(ID);
    }

    @Test
    void delete_missing_returns404() throws Exception {
        doThrow(new NoSuchElementException("Initiative not found: " + ID))
                .when(initiativeService).delete(ID);

        mockMvc.perform(delete("/api/initiatives/{id}", ID))
                .andExpect(status().isNotFound());
    }
}

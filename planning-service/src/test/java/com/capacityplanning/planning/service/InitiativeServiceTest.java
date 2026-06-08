package com.capacityplanning.planning.service;

import com.capacityplanning.planning.dto.InitiativeDto;
import com.capacityplanning.planning.model.Initiative;
import com.capacityplanning.planning.model.Initiative.InitiativeStatus;
import com.capacityplanning.planning.model.Initiative.Priority;
import com.capacityplanning.planning.repository.InitiativeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InitiativeServiceTest {

    @Mock InitiativeRepository initiativeRepository;
    @InjectMocks InitiativeService initiativeService;

    private Initiative initiative(UUID id, String name) {
        Initiative i = new Initiative();
        i.setId(id);
        i.setName(name);
        i.setDescription("Desc");
        i.setStatus(InitiativeStatus.ACTIVE);
        i.setPriority(Priority.HIGH);
        i.setTargetDate(LocalDate.of(2025, 12, 31));
        return i;
    }

    private InitiativeDto dto(String name) {
        return new InitiativeDto(null, name, "Desc",
                InitiativeStatus.ACTIVE, Priority.HIGH, LocalDate.of(2025, 12, 31), null, null);
    }

    // ── findAll ───────────────────────────────────────────────────────────────

    @Test
    void findAll_mapsEntitiesToDtos() {
        UUID id = UUID.randomUUID();
        when(initiativeRepository.findAll()).thenReturn(List.of(initiative(id, "Alpha")));

        List<InitiativeDto> result = initiativeService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(id);
        assertThat(result.get(0).name()).isEqualTo("Alpha");
        assertThat(result.get(0).status()).isEqualTo(InitiativeStatus.ACTIVE);
    }

    @Test
    void findAll_emptyRepository_returnsEmptyList() {
        when(initiativeRepository.findAll()).thenReturn(List.of());

        assertThat(initiativeService.findAll()).isEmpty();
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    void findById_found_returnsDto() {
        UUID id = UUID.randomUUID();
        when(initiativeRepository.findById(id)).thenReturn(Optional.of(initiative(id, "Beta")));

        InitiativeDto result = initiativeService.findById(id);

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.priority()).isEqualTo(Priority.HIGH);
    }

    @Test
    void findById_missing_throwsNoSuchElementException() {
        UUID id = UUID.randomUUID();
        when(initiativeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> initiativeService.findById(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining(id.toString());
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    void create_savesAndReturnsDto() {
        UUID id = UUID.randomUUID();
        Initiative saved = initiative(id, "New");
        when(initiativeRepository.save(any())).thenReturn(saved);

        InitiativeDto result = initiativeService.create(dto("New"));

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.name()).isEqualTo("New");
        verify(initiativeRepository).save(any());
    }

    @Test
    void create_doesNotSetIdOnBuilt() {
        when(initiativeRepository.save(any())).thenAnswer(inv -> {
            Initiative i = inv.getArgument(0);
            assertThat(i.getId()).isNull(); // ID must be left to DB generation
            return initiative(UUID.randomUUID(), i.getName());
        });

        initiativeService.create(dto("X"));
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    void update_replacesAllMutableFields() {
        UUID id = UUID.randomUUID();
        when(initiativeRepository.findById(id)).thenReturn(Optional.of(initiative(id, "Old")));
        when(initiativeRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InitiativeDto update = new InitiativeDto(null, "New", "Updated",
                InitiativeStatus.COMPLETED, Priority.LOW, LocalDate.of(2026, 6, 1), null, null);

        InitiativeDto result = initiativeService.update(id, update);

        assertThat(result.name()).isEqualTo("New");
        assertThat(result.description()).isEqualTo("Updated");
        assertThat(result.status()).isEqualTo(InitiativeStatus.COMPLETED);
        assertThat(result.priority()).isEqualTo(Priority.LOW);
    }

    @Test
    void update_missing_throwsNoSuchElementException() {
        UUID id = UUID.randomUUID();
        when(initiativeRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> initiativeService.update(id, dto("X")))
                .isInstanceOf(NoSuchElementException.class);
        verify(initiativeRepository, never()).save(any());
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    void delete_existingId_deletesById() {
        UUID id = UUID.randomUUID();
        when(initiativeRepository.existsById(id)).thenReturn(true);

        initiativeService.delete(id);

        verify(initiativeRepository).deleteById(id);
    }

    @Test
    void delete_missing_throwsWithoutCallingDeleteById() {
        UUID id = UUID.randomUUID();
        when(initiativeRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> initiativeService.delete(id))
                .isInstanceOf(NoSuchElementException.class);
        verify(initiativeRepository, never()).deleteById(any());
    }
}

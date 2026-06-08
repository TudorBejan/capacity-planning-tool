package com.capacityplanning.planning.service;

import com.capacityplanning.planning.dto.EpicDto;
import com.capacityplanning.planning.model.Epic;
import com.capacityplanning.planning.model.Epic.EpicStatus;
import com.capacityplanning.planning.model.Initiative;
import com.capacityplanning.planning.repository.EpicRepository;
import com.capacityplanning.planning.repository.InitiativeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EpicServiceTest {

    @Mock EpicRepository epicRepository;
    @Mock InitiativeRepository initiativeRepository;
    @InjectMocks EpicService epicService;

    private Initiative initiative() {
        Initiative i = new Initiative();
        i.setId(UUID.randomUUID());
        i.setName("Parent");
        i.setStatus(Initiative.InitiativeStatus.ACTIVE);
        i.setPriority(Initiative.Priority.HIGH);
        return i;
    }

    private Epic epic(Initiative parent) {
        Epic e = new Epic();
        e.setId(UUID.randomUUID());
        e.setInitiative(parent);
        e.setTeamId(UUID.randomUUID());
        e.setName("Auth SSO");
        e.setStatus(EpicStatus.PLANNED);
        e.setEstimatedWeeks(new BigDecimal("4.0"));
        e.setStartDate(LocalDate.of(2025, 1, 1));
        e.setDueDate(LocalDate.of(2025, 3, 31));
        return e;
    }

    private EpicDto dto(UUID initiativeId, UUID teamId) {
        return new EpicDto(null, initiativeId, teamId, null,
                "Auth SSO", "desc", EpicStatus.PLANNED, new BigDecimal("4.0"),
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31), null);
    }

    // ── findAll ───────────────────────────────────────────────────────────────

    @Test
    void findAll_mapsEntitiesToDtos() {
        Initiative parent = initiative();
        Epic e = epic(parent);
        when(epicRepository.findAll()).thenReturn(List.of(e));

        List<EpicDto> result = epicService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Auth SSO");
        assertThat(result.get(0).initiativeId()).isEqualTo(parent.getId());
    }

    // ── findById ──────────────────────────────────────────────────────────────

    @Test
    void findById_found_returnsDto() {
        Initiative parent = initiative();
        Epic e = epic(parent);
        when(epicRepository.findById(e.getId())).thenReturn(Optional.of(e));

        EpicDto result = epicService.findById(e.getId());

        assertThat(result.id()).isEqualTo(e.getId());
        assertThat(result.status()).isEqualTo(EpicStatus.PLANNED);
    }

    @Test
    void findById_missing_throwsNoSuchElementException() {
        UUID id = UUID.randomUUID();
        when(epicRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> epicService.findById(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining(id.toString());
    }

    // ── findByInitiative ──────────────────────────────────────────────────────

    @Test
    void findByInitiative_delegatesToRepository() {
        Initiative parent = initiative();
        Epic e = epic(parent);
        when(epicRepository.findByInitiativeId(parent.getId())).thenReturn(List.of(e));

        List<EpicDto> result = epicService.findByInitiative(parent.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).initiativeId()).isEqualTo(parent.getId());
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    void create_linksInitiativeAndPersists() {
        Initiative parent = initiative();
        Epic saved = epic(parent);
        when(initiativeRepository.findById(parent.getId())).thenReturn(Optional.of(parent));
        when(epicRepository.save(any())).thenReturn(saved);

        EpicDto result = epicService.create(dto(parent.getId(), saved.getTeamId()));

        assertThat(result.initiativeId()).isEqualTo(parent.getId());
        verify(epicRepository).save(any());
    }

    @Test
    void create_initiativeMissing_throwsNoSuchElementException() {
        UUID missingId = UUID.randomUUID();
        when(initiativeRepository.findById(missingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> epicService.create(dto(missingId, UUID.randomUUID())))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining(missingId.toString());
        verify(epicRepository, never()).save(any());
    }

    // ── update ────────────────────────────────────────────────────────────────

    @Test
    void update_replacesAllMutableFields() {
        Initiative parent = initiative();
        Epic e = epic(parent);
        when(epicRepository.findById(e.getId())).thenReturn(Optional.of(e));
        when(epicRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UUID newTeamId = UUID.randomUUID();
        EpicDto update = new EpicDto(null, parent.getId(), newTeamId, null,
                "Renamed", "new desc", EpicStatus.IN_PROGRESS, new BigDecimal("8.0"),
                LocalDate.of(2025, 4, 1), LocalDate.of(2025, 6, 30), null);

        EpicDto result = epicService.update(e.getId(), update);

        assertThat(result.name()).isEqualTo("Renamed");
        assertThat(result.status()).isEqualTo(EpicStatus.IN_PROGRESS);
        assertThat(result.teamId()).isEqualTo(newTeamId);
        assertThat(result.estimatedWeeks()).isEqualByComparingTo("8.0");
    }

    @Test
    void update_missing_throwsNoSuchElementException() {
        UUID id = UUID.randomUUID();
        when(epicRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> epicService.update(id, dto(UUID.randomUUID(), UUID.randomUUID())))
                .isInstanceOf(NoSuchElementException.class);
        verify(epicRepository, never()).save(any());
    }

    // ── delete ────────────────────────────────────────────────────────────────

    @Test
    void delete_existingId_deletesById() {
        UUID id = UUID.randomUUID();
        when(epicRepository.existsById(id)).thenReturn(true);

        epicService.delete(id);

        verify(epicRepository).deleteById(id);
    }

    @Test
    void delete_missing_throwsWithoutCallingDeleteById() {
        UUID id = UUID.randomUUID();
        when(epicRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> epicService.delete(id))
                .isInstanceOf(NoSuchElementException.class);
        verify(epicRepository, never()).deleteById(any());
    }
}

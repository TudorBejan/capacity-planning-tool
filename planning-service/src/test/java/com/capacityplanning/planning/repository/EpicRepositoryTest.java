package com.capacityplanning.planning.repository;

import com.capacityplanning.planning.model.Epic;
import com.capacityplanning.planning.model.Epic.EpicStatus;
import com.capacityplanning.planning.model.Initiative;
import com.capacityplanning.planning.model.Initiative.InitiativeStatus;
import com.capacityplanning.planning.model.Initiative.Priority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EpicRepositoryTest {

    @Autowired EpicRepository epicRepository;
    @Autowired InitiativeRepository initiativeRepository;

    private Initiative initiative;

    @BeforeEach
    void setUp() {
        epicRepository.deleteAll();
        initiativeRepository.deleteAll();
        initiative = initiativeRepository.save(Initiative.builder()
                .name("Parent Initiative")
                .status(InitiativeStatus.ACTIVE)
                .priority(Priority.HIGH)
                .build());
    }

    private Epic epic(String name, LocalDate start, LocalDate end) {
        return Epic.builder()
                .initiative(initiative)
                .teamId(UUID.randomUUID())
                .name(name)
                .status(EpicStatus.PLANNED)
                .estimatedWeeks(new BigDecimal("4.0"))
                .startDate(start)
                .dueDate(end)
                .build();
    }

    // ── findByInitiativeId ────────────────────────────────────────────────────

    @Test
    void findByInitiativeId_returnsOnlyEpicsForThatInitiative() {
        epicRepository.save(epic("Epic A", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 2, 28)));
        epicRepository.save(epic("Epic B", LocalDate.of(2025, 3, 1), LocalDate.of(2025, 4, 30)));

        Initiative other = initiativeRepository.save(Initiative.builder()
                .name("Other Initiative").status(InitiativeStatus.DRAFT).priority(Priority.LOW).build());
        epicRepository.save(Epic.builder()
                .initiative(other).teamId(UUID.randomUUID()).name("Other Epic")
                .status(EpicStatus.PLANNED).estimatedWeeks(new BigDecimal("2.0")).build());

        List<Epic> result = epicRepository.findByInitiativeId(initiative.getId());

        assertThat(result).hasSize(2)
                .extracting(Epic::getName)
                .containsExactlyInAnyOrder("Epic A", "Epic B");
    }

    // ── findOverlappingEpics ──────────────────────────────────────────────────

    @Test
    void findOverlappingEpics_returnsEpicThatSpansRange() {
        epicRepository.save(epic("Spans range",
                LocalDate.of(2025, 1, 15), LocalDate.of(2025, 3, 15)));

        List<Epic> result = epicRepository.findOverlappingEpics(
                LocalDate.of(2025, 2, 1), LocalDate.of(2025, 4, 30));

        assertThat(result).hasSize(1).extracting(Epic::getName).containsExactly("Spans range");
    }

    @Test
    void findOverlappingEpics_excludesEpicBeforeRange() {
        epicRepository.save(epic("Before range",
                LocalDate.of(2024, 10, 1), LocalDate.of(2024, 12, 31)));

        List<Epic> result = epicRepository.findOverlappingEpics(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));

        assertThat(result).isEmpty();
    }

    @Test
    void findOverlappingEpics_excludesEpicAfterRange() {
        epicRepository.save(epic("After range",
                LocalDate.of(2025, 5, 1), LocalDate.of(2025, 6, 30)));

        List<Epic> result = epicRepository.findOverlappingEpics(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));

        assertThat(result).isEmpty();
    }

    @Test
    void findOverlappingEpics_epicStartingOnEndDateIsIncluded() {
        epicRepository.save(epic("Starts on end",
                LocalDate.of(2025, 3, 31), LocalDate.of(2025, 5, 1)));

        List<Epic> result = epicRepository.findOverlappingEpics(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));

        assertThat(result).hasSize(1);
    }

    @Test
    void findOverlappingEpics_epicEndingOnStartDateIsIncluded() {
        epicRepository.save(epic("Ends on start",
                LocalDate.of(2024, 12, 1), LocalDate.of(2025, 1, 1)));

        List<Epic> result = epicRepository.findOverlappingEpics(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));

        assertThat(result).hasSize(1);
    }

    @Test
    void findOverlappingEpics_nullStartAndEndDate_treatedAsOpenEnded() {
        epicRepository.save(epic("No dates", null, null));

        List<Epic> result = epicRepository.findOverlappingEpics(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));

        assertThat(result).hasSize(1);
    }

    @Test
    void findOverlappingEpics_nullStartDate_matchesIfDueDateInRange() {
        epicRepository.save(epic("No start", null, LocalDate.of(2025, 2, 28)));

        List<Epic> result = epicRepository.findOverlappingEpics(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));

        assertThat(result).hasSize(1);
    }

    @Test
    void findOverlappingEpics_nullDueDate_matchesIfStartDateInRange() {
        epicRepository.save(epic("No end", LocalDate.of(2025, 2, 1), null));

        List<Epic> result = epicRepository.findOverlappingEpics(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));

        assertThat(result).hasSize(1);
    }

    @Test
    void findOverlappingEpics_mixedResults() {
        epicRepository.save(epic("In range",   LocalDate.of(2025, 1, 15), LocalDate.of(2025, 2, 28)));
        epicRepository.save(epic("Before",     LocalDate.of(2024, 10, 1), LocalDate.of(2024, 12, 31)));
        epicRepository.save(epic("After",      LocalDate.of(2025, 5, 1),  LocalDate.of(2025, 6, 30)));

        List<Epic> result = epicRepository.findOverlappingEpics(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31));

        assertThat(result).hasSize(1).extracting(Epic::getName).containsExactly("In range");
    }
}

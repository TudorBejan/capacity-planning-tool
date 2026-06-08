package com.capacityplanning.planning.repository;

import com.capacityplanning.planning.model.Epic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface EpicRepository extends JpaRepository<Epic, UUID> {

    List<Epic> findByInitiativeId(UUID initiativeId);

    List<Epic> findByTeamId(UUID teamId);

    /**
     * Epics that overlap with the given date range.
     * An epic overlaps if its start_date <= endDate AND due_date >= startDate.
     */
    @Query("""
            SELECT e FROM Epic e
            WHERE (e.startDate IS NULL OR e.startDate <= :endDate)
              AND (e.dueDate IS NULL OR e.dueDate >= :startDate)
            """)
    List<Epic> findOverlappingEpics(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}

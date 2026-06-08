package com.capacityplanning.planning.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "epics")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Epic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiative_id")
    private Initiative initiative;

    /** References a Team in the team-service; no FK constraint across services. */
    @Column(name = "team_id", nullable = false)
    private UUID teamId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EpicStatus status;

    /** Developer effort estimate in person-weeks. */
    @Column(name = "estimated_weeks", nullable = false)
    private BigDecimal estimatedWeeks;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum EpicStatus { PLANNED, IN_PROGRESS, COMPLETED, CANCELLED }
}

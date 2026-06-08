package com.capacityplanning.planning.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "initiatives")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Initiative {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InitiativeStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @OneToMany(mappedBy = "initiative", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Epic> epics = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum InitiativeStatus { DRAFT, ACTIVE, COMPLETED, CANCELLED }
    public enum Priority { LOW, MEDIUM, HIGH, CRITICAL }
}

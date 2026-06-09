package com.capacityplanning.planning.service;

import com.capacityplanning.planning.dto.EpicDto;
import com.capacityplanning.planning.model.Epic;
import com.capacityplanning.planning.model.Initiative;
import com.capacityplanning.planning.repository.EpicRepository;
import com.capacityplanning.planning.repository.InitiativeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EpicService {

    private final EpicRepository epicRepository;
    private final InitiativeRepository initiativeRepository;

    public List<EpicDto> findAll() {
        return epicRepository.findAll().stream().map(this::toDto).toList();
    }

    public EpicDto findById(UUID id) {
        return epicRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("Epic not found: " + id));
    }

    public List<EpicDto> findByInitiative(UUID initiativeId) {
        return epicRepository.findByInitiativeId(initiativeId).stream().map(this::toDto).toList();
    }

    public List<EpicDto> findByTeam(UUID teamId) {
        return epicRepository.findByTeamId(teamId).stream().map(this::toDto).toList();
    }

    @Transactional
    public EpicDto create(EpicDto dto) {
        Initiative initiative = initiativeRepository.findById(dto.initiativeId())
                .orElseThrow(() -> new NoSuchElementException("Initiative not found: " + dto.initiativeId()));
        Epic epic = Epic.builder()
                .initiative(initiative)
                .teamId(dto.teamId())
                .name(dto.name())
                .description(dto.description())
                .status(dto.status())
                .estimatedWeeks(dto.estimatedWeeks())
                .startDate(dto.startDate())
                .dueDate(dto.dueDate())
                .build();
        return toDto(epicRepository.save(epic));
    }

    @Transactional
    public EpicDto update(UUID id, EpicDto dto) {
        Epic epic = epicRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Epic not found: " + id));
        epic.setTeamId(dto.teamId());
        epic.setName(dto.name());
        epic.setDescription(dto.description());
        epic.setStatus(dto.status());
        epic.setEstimatedWeeks(dto.estimatedWeeks());
        epic.setStartDate(dto.startDate());
        epic.setDueDate(dto.dueDate());
        return toDto(epicRepository.save(epic));
    }

    @Transactional
    public void delete(UUID id) {
        if (!epicRepository.existsById(id)) {
            throw new NoSuchElementException("Epic not found: " + id);
        }
        epicRepository.deleteById(id);
    }

    EpicDto toDto(Epic e) {
        return new EpicDto(e.getId(), e.getInitiative().getId(), e.getTeamId(), null,
                e.getName(), e.getDescription(), e.getStatus(),
                e.getEstimatedWeeks(), e.getStartDate(), e.getDueDate(), e.getCreatedAt());
    }
}

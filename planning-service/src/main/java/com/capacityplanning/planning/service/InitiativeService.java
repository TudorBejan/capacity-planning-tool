package com.capacityplanning.planning.service;

import com.capacityplanning.planning.dto.EpicDto;
import com.capacityplanning.planning.dto.InitiativeDto;
import com.capacityplanning.planning.model.Epic;
import com.capacityplanning.planning.model.Initiative;
import com.capacityplanning.planning.repository.InitiativeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InitiativeService {

    private final InitiativeRepository initiativeRepository;

    public List<InitiativeDto> findAll() {
        return initiativeRepository.findAll().stream().map(this::toDto).toList();
    }

    public InitiativeDto findById(UUID id) {
        return initiativeRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("Initiative not found: " + id));
    }

    @Transactional
    public InitiativeDto create(InitiativeDto dto) {
        Initiative initiative = Initiative.builder()
                .name(dto.name())
                .description(dto.description())
                .status(dto.status())
                .priority(dto.priority())
                .targetDate(dto.targetDate())
                .build();
        return toDto(initiativeRepository.save(initiative));
    }

    @Transactional
    public InitiativeDto update(UUID id, InitiativeDto dto) {
        Initiative initiative = initiativeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Initiative not found: " + id));
        initiative.setName(dto.name());
        initiative.setDescription(dto.description());
        initiative.setStatus(dto.status());
        initiative.setPriority(dto.priority());
        initiative.setTargetDate(dto.targetDate());
        return toDto(initiativeRepository.save(initiative));
    }

    @Transactional
    public void delete(UUID id) {
        if (!initiativeRepository.existsById(id)) {
            throw new NoSuchElementException("Initiative not found: " + id);
        }
        initiativeRepository.deleteById(id);
    }

    InitiativeDto toDto(Initiative i) {
        List<EpicDto> epicDtos = i.getEpics().stream().map(this::epicToDto).toList();
        return new InitiativeDto(i.getId(), i.getName(), i.getDescription(),
                i.getStatus(), i.getPriority(), i.getTargetDate(), epicDtos, i.getCreatedAt());
    }

    private EpicDto epicToDto(Epic e) {
        return new EpicDto(e.getId(), e.getInitiative().getId(), e.getTeamId(), null,
                e.getName(), e.getDescription(), e.getStatus(),
                e.getEstimatedWeeks(), e.getStartDate(), e.getDueDate(), e.getCreatedAt());
    }
}

package com.capacityplanning.planning.controller;

import com.capacityplanning.planning.dto.EpicDto;
import com.capacityplanning.planning.service.EpicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/epics")
@RequiredArgsConstructor
public class EpicController {

    private final EpicService epicService;

    @GetMapping
    public List<EpicDto> getAll() {
        return epicService.findAll();
    }

    @GetMapping("/{id}")
    public EpicDto getById(@PathVariable UUID id) {
        return epicService.findById(id);
    }

    @GetMapping("/by-initiative/{initiativeId}")
    public List<EpicDto> getByInitiative(@PathVariable UUID initiativeId) {
        return epicService.findByInitiative(initiativeId);
    }

    @GetMapping("/by-team/{teamId}")
    public List<EpicDto> getByTeam(@PathVariable UUID teamId) {
        return epicService.findByTeam(teamId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EpicDto create(@Valid @RequestBody EpicDto dto) {
        return epicService.create(dto);
    }

    @PutMapping("/{id}")
    public EpicDto update(@PathVariable UUID id, @Valid @RequestBody EpicDto dto) {
        return epicService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        epicService.delete(id);
    }
}

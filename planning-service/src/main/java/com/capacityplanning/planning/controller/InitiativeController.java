package com.capacityplanning.planning.controller;

import com.capacityplanning.planning.dto.InitiativeDto;
import com.capacityplanning.planning.service.InitiativeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/initiatives")
@RequiredArgsConstructor
public class InitiativeController {

    private final InitiativeService initiativeService;

    @GetMapping
    public List<InitiativeDto> getAll() {
        return initiativeService.findAll();
    }

    @GetMapping("/{id}")
    public InitiativeDto getById(@PathVariable UUID id) {
        return initiativeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InitiativeDto create(@Valid @RequestBody InitiativeDto dto) {
        return initiativeService.create(dto);
    }

    @PutMapping("/{id}")
    public InitiativeDto update(@PathVariable UUID id, @Valid @RequestBody InitiativeDto dto) {
        return initiativeService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        initiativeService.delete(id);
    }
}

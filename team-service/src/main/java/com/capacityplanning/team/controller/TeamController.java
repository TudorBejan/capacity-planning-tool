package com.capacityplanning.team.controller;

import com.capacityplanning.team.dto.TeamCapacityDto;
import com.capacityplanning.team.dto.TeamDto;
import com.capacityplanning.team.service.CapacityService;
import com.capacityplanning.team.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final CapacityService capacityService;

    @GetMapping
    public List<TeamDto> getAll() {
        return teamService.findAll();
    }

    @GetMapping("/{id}")
    public TeamDto getById(@PathVariable UUID id) {
        return teamService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TeamDto create(@Valid @RequestBody TeamDto dto) {
        return teamService.create(dto);
    }

    @PutMapping("/{id}")
    public TeamDto update(@PathVariable UUID id, @Valid @RequestBody TeamDto dto) {
        return teamService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        teamService.delete(id);
    }

    @GetMapping("/{id}/capacity")
    public TeamCapacityDto getCapacity(
            @PathVariable UUID id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return capacityService.calculateCapacity(id, startDate, endDate);
    }
}

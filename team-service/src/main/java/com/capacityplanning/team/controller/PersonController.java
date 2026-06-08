package com.capacityplanning.team.controller;

import com.capacityplanning.team.dto.PersonDto;
import com.capacityplanning.team.service.PersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @GetMapping("/by-team/{teamId}")
    public List<PersonDto> getByTeam(@PathVariable UUID teamId) {
        return personService.findByTeamId(teamId);
    }

    @GetMapping("/{id}")
    public PersonDto getById(@PathVariable UUID id) {
        return personService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonDto create(@Valid @RequestBody PersonDto dto) {
        return personService.create(dto);
    }

    @PutMapping("/{id}")
    public PersonDto update(@PathVariable UUID id, @Valid @RequestBody PersonDto dto) {
        return personService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        personService.delete(id);
    }
}

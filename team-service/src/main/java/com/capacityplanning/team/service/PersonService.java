package com.capacityplanning.team.service;

import com.capacityplanning.team.dto.PersonDto;
import com.capacityplanning.team.model.Person;
import com.capacityplanning.team.model.Team;
import com.capacityplanning.team.repository.PersonRepository;
import com.capacityplanning.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final TeamRepository teamRepository;

    public List<PersonDto> findByTeamId(UUID teamId) {
        return personRepository.findByTeamId(teamId).stream().map(this::toDto).toList();
    }

    public PersonDto findById(UUID id) {
        return personRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("Person not found: " + id));
    }

    @Transactional
    public PersonDto create(PersonDto dto) {
        Team team = teamRepository.findById(dto.teamId())
                .orElseThrow(() -> new NoSuchElementException("Team not found: " + dto.teamId()));
        Person person = Person.builder()
                .team(team)
                .name(dto.name())
                .role(dto.role())
                .availabilityPercentage(dto.availabilityPercentage())
                .build();
        return toDto(personRepository.save(person));
    }

    @Transactional
    public PersonDto update(UUID id, PersonDto dto) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Person not found: " + id));
        if (!person.getTeam().getId().equals(dto.teamId())) {
            Team newTeam = teamRepository.findById(dto.teamId())
                    .orElseThrow(() -> new NoSuchElementException("Team not found: " + dto.teamId()));
            person.setTeam(newTeam);
        }
        person.setName(dto.name());
        person.setRole(dto.role());
        person.setAvailabilityPercentage(dto.availabilityPercentage());
        return toDto(personRepository.save(person));
    }

    @Transactional
    public void delete(UUID id) {
        if (!personRepository.existsById(id)) {
            throw new NoSuchElementException("Person not found: " + id);
        }
        personRepository.deleteById(id);
    }

    private PersonDto toDto(Person p) {
        return new PersonDto(p.getId(), p.getTeam().getId(), p.getName(),
                p.getRole(), p.getAvailabilityPercentage(), p.getCreatedAt());
    }
}

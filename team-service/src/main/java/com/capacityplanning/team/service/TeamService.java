package com.capacityplanning.team.service;

import com.capacityplanning.team.dto.PersonDto;
import com.capacityplanning.team.dto.TeamDto;
import com.capacityplanning.team.model.Person;
import com.capacityplanning.team.model.Team;
import com.capacityplanning.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    public List<TeamDto> findAll() {
        return teamRepository.findAll().stream().map(this::toDto).toList();
    }

    public TeamDto findById(UUID id) {
        return teamRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("Team not found: " + id));
    }

    @Transactional
    public TeamDto create(TeamDto dto) {
        Team team = Team.builder()
                .name(dto.name())
                .description(dto.description())
                .overheadPercentage(dto.overheadPercentage())
                .build();
        return toDto(teamRepository.save(team));
    }

    @Transactional
    public TeamDto update(UUID id, TeamDto dto) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Team not found: " + id));
        team.setName(dto.name());
        team.setDescription(dto.description());
        team.setOverheadPercentage(dto.overheadPercentage());
        return toDto(teamRepository.save(team));
    }

    @Transactional
    public void delete(UUID id) {
        if (!teamRepository.existsById(id)) {
            throw new NoSuchElementException("Team not found: " + id);
        }
        teamRepository.deleteById(id);
    }

    public TeamDto toDto(Team team) {
        List<PersonDto> personDtos = team.getPersons().stream()
                .map(this::personToDto)
                .toList();
        return new TeamDto(
                team.getId(),
                team.getName(),
                team.getDescription(),
                team.getOverheadPercentage(),
                personDtos,
                team.getCreatedAt()
        );
    }

    private PersonDto personToDto(Person p) {
        return new PersonDto(p.getId(), p.getTeam().getId(), p.getName(),
                p.getRole(), p.getAvailabilityPercentage(), p.getCreatedAt());
    }
}

package com.capacityplanning.team.repository;

import com.capacityplanning.team.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID> {
    List<Person> findByTeamId(UUID teamId);
    void deleteByTeamId(UUID teamId);
}

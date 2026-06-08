package com.capacityplanning.team.repository;

import com.capacityplanning.team.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {}

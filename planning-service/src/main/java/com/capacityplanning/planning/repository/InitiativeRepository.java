package com.capacityplanning.planning.repository;

import com.capacityplanning.planning.model.Initiative;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InitiativeRepository extends JpaRepository<Initiative, UUID> {}

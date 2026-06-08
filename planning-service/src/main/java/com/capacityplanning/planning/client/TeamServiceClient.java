package com.capacityplanning.planning.client;

import com.capacityplanning.planning.dto.TeamCapacityDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.UUID;

@Component
@Slf4j
public class TeamServiceClient {

    private final RestClient restClient;

    public TeamServiceClient(@Value("${team-service.url}") String teamServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(teamServiceUrl)
                .build();
    }

    public TeamCapacityDto getTeamCapacity(UUID teamId, LocalDate startDate, LocalDate endDate) {
        try {
            return restClient.get()
                    .uri("/api/teams/{id}/capacity?startDate={start}&endDate={end}",
                            teamId, startDate, endDate)
                    .retrieve()
                    .body(TeamCapacityDto.class);
        } catch (Exception e) {
            log.warn("Could not fetch capacity for team {}: {}", teamId, e.getMessage());
            return null;
        }
    }
}

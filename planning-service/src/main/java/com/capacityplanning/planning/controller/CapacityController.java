package com.capacityplanning.planning.controller;

import com.capacityplanning.planning.dto.CapacitySummaryDto;
import com.capacityplanning.planning.service.CapacityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/capacity")
@RequiredArgsConstructor
public class CapacityController {

    private final CapacityService capacityService;

    @GetMapping("/summary")
    public CapacitySummaryDto getSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return capacityService.getSummary(startDate, endDate);
    }
}

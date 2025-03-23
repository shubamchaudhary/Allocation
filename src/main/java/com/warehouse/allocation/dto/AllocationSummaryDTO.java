package com.warehouse.allocation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocationSummaryDTO {

    private UUID allocationSetId;

    private String summarySteps;

    private String suggestedImprovements;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
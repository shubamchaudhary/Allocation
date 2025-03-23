package com.warehouse.allocation.dto;

import com.warehouse.allocation.model.AllocationSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AllocationSetDTO {

    private UUID id;

    @NotBlank(message = "Name is required")
    private String name;

    private AllocationSet.Status status;

    @NotNull(message = "Need variable ID is required")
    private UUID needVariableId;

    private String needVariableName;

    @NotNull(message = "Priority variable ID is required")
    private UUID priorityVariableId;

    private String priorityVariableName;

    private Boolean allocateTogether;

    @NotEmpty(message = "At least one supply must be selected")
    private Set<UUID> supplyIds;

    @NotEmpty(message = "At least one destination must be selected")
    private Set<UUID> destinationIds;

    private LocalDateTime scheduledTime;

    private LocalDateTime completedTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
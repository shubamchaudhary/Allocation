package com.warehouse.allocation.dto;

import com.warehouse.allocation.model.Allocation;
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
public class AllocationDTO {

    private UUID id;

    private UUID allocationSetId;

    private UUID supplyId;

    private UUID destinationId;

    private String itemStyleColor;

    private String warehouseName;

    private String destinationName;

    private Integer allocatedQuantity;

    private Integer allocatedPacks;

    private Allocation.TransportMode transportMode;

    private Double transportCost;

    private LocalDateTime createdAt;
}
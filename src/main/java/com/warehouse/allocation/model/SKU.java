package com.warehouse.allocation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SKU {
    private UUID itemId;
    private UUID locationId;
    private Integer priority;
    private Double need;
    private String hierarchyLevel;
}
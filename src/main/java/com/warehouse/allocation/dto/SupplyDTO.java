package com.warehouse.allocation.dto;

import com.warehouse.allocation.model.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplyDTO {

    private UUID id;

    @NotNull(message = "Item ID is required")
    private UUID itemId;

    @NotNull(message = "Warehouse ID is required")
    private UUID warehouseId;

    private String itemStyleColor;

    private String warehouseName;

    @NotNull(message = "Available quantity is required")
    @Min(value = 0, message = "Available quantity must be non-negative")
    private Integer availableQuantity;

    private Integer numPacks;

    private LocalDateTime availableFrom;

    private Item.ItemType itemType;

    private Integer packRatio;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
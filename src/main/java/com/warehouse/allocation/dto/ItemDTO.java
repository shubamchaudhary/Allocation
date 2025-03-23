package com.warehouse.allocation.dto;

import com.warehouse.allocation.model.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {

    private UUID id;

    @NotBlank(message = "Style color is required")
    private String styleColor;

    @NotBlank(message = "Style is required")
    private String style;

    @NotBlank(message = "Item class is required")
    private String itemClass;

    private String description;

    @NotNull(message = "Item type is required")
    private Item.ItemType type;

    private Integer packRatio;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
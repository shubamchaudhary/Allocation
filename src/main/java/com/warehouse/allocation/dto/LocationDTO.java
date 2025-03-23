package com.warehouse.allocation.dto;

import com.warehouse.allocation.model.Location;
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
public class LocationDTO {

    private UUID id;

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Location type is required")
    private Location.LocationType type;

    private Double latitude;

    private Double longitude;

    private Integer minShipQuantity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
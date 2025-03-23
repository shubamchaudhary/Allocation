package com.warehouse.allocation.controller;

import com.warehouse.allocation.dto.LocationDTO;
import com.warehouse.allocation.model.Location;
import com.warehouse.allocation.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable UUID id) {
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<LocationDTO> getLocationByCode(@PathVariable String code) {
        return ResponseEntity.ok(locationService.getLocationByCode(code));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<LocationDTO>> getLocationsByType(@PathVariable Location.LocationType type) {
        return ResponseEntity.ok(locationService.getLocationsByType(type));
    }

    @GetMapping("/stores")
    public ResponseEntity<List<LocationDTO>> getStores() {
        return ResponseEntity.ok(locationService.getLocationsByType(Location.LocationType.STORE));
    }

    @GetMapping("/warehouses")
    public ResponseEntity<List<LocationDTO>> getWarehouses() {
        return ResponseEntity.ok(locationService.getLocationsByType(Location.LocationType.WAREHOUSE));
    }

    @GetMapping("/stores/min-ship/{quantity}")
    public ResponseEntity<List<LocationDTO>> getStoresWithMinShipQuantity(@PathVariable Integer quantity) {
        return ResponseEntity.ok(locationService.getStoresWithMinShipQuantity(quantity));
    }

    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(@Valid @RequestBody LocationDTO locationDTO) {
        return new ResponseEntity<>(locationService.createLocation(locationDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> updateLocation(@PathVariable UUID id, @Valid @RequestBody LocationDTO locationDTO) {
        return ResponseEntity.ok(locationService.updateLocation(id, locationDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable UUID id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
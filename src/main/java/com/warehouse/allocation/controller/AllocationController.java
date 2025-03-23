package com.warehouse.allocation.controller;

import com.warehouse.allocation.dto.AllocationDTO;
import com.warehouse.allocation.dto.AllocationSetDTO;
import com.warehouse.allocation.dto.AllocationSummaryDTO;
import com.warehouse.allocation.model.AllocationSet;
import com.warehouse.allocation.service.AllocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/allocations")
@RequiredArgsConstructor
public class AllocationController {

    private final AllocationService allocationService;

    @GetMapping("/sets")
    public ResponseEntity<List<AllocationSetDTO>> getAllAllocationSets() {
        return ResponseEntity.ok(allocationService.getAllAllocationSets());
    }

    @GetMapping("/sets/recent")
    public ResponseEntity<List<AllocationSetDTO>> getRecentAllocationSets() {
        return ResponseEntity.ok(allocationService.getRecentAllocationSets());
    }

    @GetMapping("/sets/{id}")
    public ResponseEntity<AllocationSetDTO> getAllocationSetById(@PathVariable UUID id) {
        return ResponseEntity.ok(allocationService.getAllocationSetById(id));
    }

    @GetMapping("/sets/{id}/allocations")
    public ResponseEntity<List<AllocationDTO>> getAllocationsBySetId(@PathVariable UUID id) {
        return ResponseEntity.ok(allocationService.getAllocationsBySetId(id));
    }

    @GetMapping("/sets/{id}/summary")
    public ResponseEntity<AllocationSummaryDTO> getAllocationSummary(@PathVariable UUID id) {
        return ResponseEntity.ok(allocationService.getAllocationSummary(id));
    }

    @PostMapping("/sets")
    public ResponseEntity<AllocationSetDTO> createAllocationSet(@Valid @RequestBody AllocationSetDTO allocationSetDTO) {
        return new ResponseEntity<>(allocationService.createAllocationSet(allocationSetDTO), HttpStatus.CREATED);
    }

    @PutMapping("/sets/{id}/status")
    public ResponseEntity<AllocationSetDTO> updateAllocationSetStatus(
            @PathVariable UUID id,
            @RequestParam AllocationSet.Status status) {
        return ResponseEntity.ok(allocationService.updateAllocationSetStatus(id, status));
    }

    @PostMapping("/sets/{id}/execute")
    public ResponseEntity<Void> executeAllocation(@PathVariable UUID id) {
        allocationService.executeAllocation(id);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/sets/{id}")
    public ResponseEntity<Void> deleteAllocationSet(@PathVariable UUID id) {
        allocationService.deleteAllocationSet(id);
        return ResponseEntity.noContent().build();
    }
}
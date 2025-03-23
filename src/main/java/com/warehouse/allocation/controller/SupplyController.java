package com.warehouse.allocation.controller;

import com.warehouse.allocation.dto.SupplyDTO;
import com.warehouse.allocation.service.SupplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/supplies")
@RequiredArgsConstructor
public class SupplyController {

    private final SupplyService supplyService;

    @GetMapping
    public ResponseEntity<List<SupplyDTO>> getAllSupplies() {
        return ResponseEntity.ok(supplyService.getAllSupplies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplyDTO> getSupplyById(@PathVariable UUID id) {
        return ResponseEntity.ok(supplyService.getSupplyById(id));
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<SupplyDTO>> getSuppliesByItem(@PathVariable UUID itemId) {
        return ResponseEntity.ok(supplyService.getSuppliesByItem(itemId));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<SupplyDTO>> getSuppliesByWarehouse(@PathVariable UUID warehouseId) {
        return ResponseEntity.ok(supplyService.getSuppliesByWarehouse(warehouseId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<SupplyDTO>> getAvailableSupplies() {
        return ResponseEntity.ok(supplyService.getAvailableSupplies());
    }

    @PostMapping
    public ResponseEntity<SupplyDTO> createSupply(@Valid @RequestBody SupplyDTO supplyDTO) {
        return new ResponseEntity<>(supplyService.createSupply(supplyDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplyDTO> updateSupply(@PathVariable UUID id, @Valid @RequestBody SupplyDTO supplyDTO) {
        return ResponseEntity.ok(supplyService.updateSupply(id, supplyDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupply(@PathVariable UUID id) {
        supplyService.deleteSupply(id);
        return ResponseEntity.noContent().build();
    }
}
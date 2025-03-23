package com.warehouse.allocation.service;

import com.warehouse.allocation.dto.SupplyDTO;
import com.warehouse.allocation.exception.ResourceNotFoundException;
import com.warehouse.allocation.model.Item;
import com.warehouse.allocation.model.Location;
import com.warehouse.allocation.model.Supply;
import com.warehouse.allocation.repository.ItemRepository;
import com.warehouse.allocation.repository.LocationRepository;
import com.warehouse.allocation.repository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupplyService {

    private final SupplyRepository supplyRepository;
    private final ItemRepository itemRepository;
    private final LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public List<SupplyDTO> getAllSupplies() {
        return supplyRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SupplyDTO getSupplyById(UUID id) {
        return supplyRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Supply not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<SupplyDTO> getSuppliesByItem(UUID itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + itemId));

        return supplyRepository.findByItem(item).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupplyDTO> getSuppliesByWarehouse(UUID warehouseId) {
        Location warehouse = locationRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + warehouseId));

        return supplyRepository.findByWarehouse(warehouse).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SupplyDTO> getAvailableSupplies() {
        return supplyRepository.findAvailableSupplies(LocalDateTime.now()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SupplyDTO createSupply(SupplyDTO supplyDTO) {
        Item item = itemRepository.findById(supplyDTO.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + supplyDTO.getItemId()));

        Location warehouse = locationRepository.findById(supplyDTO.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + supplyDTO.getWarehouseId()));

        Supply supply = Supply.builder()
                .item(item)
                .warehouse(warehouse)
                .availableQuantity(supplyDTO.getAvailableQuantity())
                .numPacks(supplyDTO.getNumPacks())
                .availableFrom(supplyDTO.getAvailableFrom())
                .build();

        Supply savedSupply = supplyRepository.save(supply);
        return mapToDTO(savedSupply);
    }

    @Transactional
    public SupplyDTO updateSupply(UUID id, SupplyDTO supplyDTO) {
        Supply existingSupply = supplyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supply not found with id: " + id));

        Item item = itemRepository.findById(supplyDTO.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + supplyDTO.getItemId()));

        Location warehouse = locationRepository.findById(supplyDTO.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with id: " + supplyDTO.getWarehouseId()));

        existingSupply.setItem(item);
        existingSupply.setWarehouse(warehouse);
        existingSupply.setAvailableQuantity(supplyDTO.getAvailableQuantity());
        existingSupply.setNumPacks(supplyDTO.getNumPacks());
        existingSupply.setAvailableFrom(supplyDTO.getAvailableFrom());

        Supply updatedSupply = supplyRepository.save(existingSupply);
        return mapToDTO(updatedSupply);
    }

    @Transactional
    public void deleteSupply(UUID id) {
        if (!supplyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supply not found with id: " + id);
        }
        supplyRepository.deleteById(id);
    }

    private SupplyDTO mapToDTO(Supply supply) {
        return SupplyDTO.builder()
                .id(supply.getId())
                .itemId(supply.getItem().getId())
                .warehouseId(supply.getWarehouse().getId())
                .itemStyleColor(supply.getItem().getStyleColor())
                .warehouseName(supply.getWarehouse().getName())
                .availableQuantity(supply.getAvailableQuantity())
                .numPacks(supply.getNumPacks())
                .availableFrom(supply.getAvailableFrom())
                .itemType(supply.getItem().getType())
                .packRatio(supply.getItem().getPackRatio())
                .createdAt(supply.getCreatedAt())
                .updatedAt(supply.getUpdatedAt())
                .build();
    }
}
package com.warehouse.allocation.service;

import com.warehouse.allocation.dto.AllocationDTO;
import com.warehouse.allocation.dto.AllocationSetDTO;
import com.warehouse.allocation.dto.AllocationSummaryDTO;
import com.warehouse.allocation.exception.ResourceNotFoundException;
import com.warehouse.allocation.model.*;
import com.warehouse.allocation.repository.*;
import com.warehouse.allocation.service.allocation.BulkAllocationStrategy;
import com.warehouse.allocation.service.allocation.PackAllocationStrategy;
import com.warehouse.allocation.service.llm.LlmService;
import com.warehouse.allocation.service.need.NeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AllocationService {

    private final AllocationSetRepository allocationSetRepository;
    private final AllocationRepository allocationRepository;
    private final AllocationSummaryRepository allocationSummaryRepository;
    private final SupplyRepository supplyRepository;
    private final LocationRepository locationRepository;
    private final NeedVariableRepository needVariableRepository;
    private final PriorityVariableRepository priorityVariableRepository;
    private final NeedService needService;
    private final BulkAllocationStrategy bulkAllocationStrategy;
    private final PackAllocationStrategy packAllocationStrategy;
    private final LlmService llmService;

    @Transactional(readOnly = true)
    public List<AllocationSetDTO> getAllAllocationSets() {
        return allocationSetRepository.findAll().stream()
                .map(this::mapToAllocationSetDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AllocationSetDTO getAllocationSetById(UUID id) {
        return allocationSetRepository.findById(id)
                .map(this::mapToAllocationSetDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation set not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<AllocationSetDTO> getRecentAllocationSets() {
        return allocationSetRepository.findTop10ByOrderByCreatedAtDesc().stream()
                .map(this::mapToAllocationSetDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AllocationDTO> getAllocationsBySetId(UUID allocationSetId) {
        AllocationSet allocationSet = allocationSetRepository.findById(allocationSetId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation set not found with id: " + allocationSetId));

        return allocationRepository.findByAllocationSet(allocationSet).stream()
                .map(this::mapToAllocationDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AllocationSummaryDTO getAllocationSummary(UUID allocationSetId) {
        return allocationSummaryRepository.findById(allocationSetId)
                .map(this::mapToAllocationSummaryDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation summary not found for set with id: " + allocationSetId));
    }

    @Transactional
    public AllocationSetDTO createAllocationSet(AllocationSetDTO allocationSetDTO) {
        // Validate need and priority variables
        NeedVariable needVariable = needVariableRepository.findById(allocationSetDTO.getNeedVariableId())
                .orElseThrow(() -> new ResourceNotFoundException("Need variable not found with id: " + allocationSetDTO.getNeedVariableId()));

        PriorityVariable priorityVariable = priorityVariableRepository.findById(allocationSetDTO.getPriorityVariableId())
                .orElseThrow(() -> new ResourceNotFoundException("Priority variable not found with id: " + allocationSetDTO.getPriorityVariableId()));

        // Create the allocation set
        AllocationSet allocationSet = AllocationSet.builder()
                .name(allocationSetDTO.getName())
                .status(AllocationSet.Status.DRAFT)
                .needVariable(needVariable)
                .priorityVariable(priorityVariable)
                .allocateTogether(allocationSetDTO.getAllocateTogether())
                .build();

        // Add supplies
        Set<Supply> supplies = new HashSet<>();
        for (UUID supplyId : allocationSetDTO.getSupplyIds()) {
            Supply supply = supplyRepository.findById(supplyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Supply not found with id: " + supplyId));
            supplies.add(supply);
        }
        allocationSet.setSupplies(supplies);

        // Add destinations
        Set<Location> destinations = new HashSet<>();
        for (UUID locationId : allocationSetDTO.getDestinationIds()) {
            Location location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + locationId));

            if (location.getType() != Location.LocationType.STORE) {
                throw new IllegalArgumentException("Location with id: " + locationId + " is not a store");
            }

            destinations.add(location);
        }
        allocationSet.setDestinations(destinations);

        // Save the allocation set
        AllocationSet savedAllocationSet = allocationSetRepository.save(allocationSet);

        return mapToAllocationSetDTO(savedAllocationSet);
    }

    @Transactional
    public AllocationSetDTO updateAllocationSetStatus(UUID id, AllocationSet.Status status) {
        AllocationSet allocationSet = allocationSetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation set not found with id: " + id));

        allocationSet.setStatus(status);

        if (status == AllocationSet.Status.SCHEDULED) {
            allocationSet.setScheduledTime(LocalDateTime.now().plusMinutes(1)); // Schedule for 1 minute later
        } else if (status == AllocationSet.Status.COMPLETED) {
            allocationSet.setCompletedTime(LocalDateTime.now());
        }

        AllocationSet updatedAllocationSet = allocationSetRepository.save(allocationSet);
        return mapToAllocationSetDTO(updatedAllocationSet);
    }

    @Transactional
    public void deleteAllocationSet(UUID id) {
        if (!allocationSetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Allocation set not found with id: " + id);
        }

        allocationSetRepository.deleteById(id);
    }

    @Transactional
    public void executeAllocation(UUID allocationSetId) {
        AllocationSet allocationSet = allocationSetRepository.findById(allocationSetId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation set not found with id: " + allocationSetId));

        if (allocationSet.getStatus() != AllocationSet.Status.SCHEDULED &&
                allocationSet.getStatus() != AllocationSet.Status.DRAFT) {
            throw new IllegalStateException("Allocation set is not in a schedulable state");
        }

        try {
            // Update status to in progress
            allocationSet.setStatus(AllocationSet.Status.IN_PROGRESS);
            allocationSetRepository.save(allocationSet);

            // Collect all item IDs and location IDs for need calculation
            List<UUID> itemIds = allocationSet.getSupplies().stream()
                    .map(supply -> supply.getItem().getId())
                    .collect(Collectors.toList());

            List<UUID> locationIds = allocationSet.getDestinations().stream()
                    .map(Location::getId)
                    .collect(Collectors.toList());

            // Calculate needs
            List<SKU> skus = needService.calculateNeed(itemIds, locationIds, allocationSet.getNeedVariable());

            // Perform allocation based on item types
            List<Allocation> allocations = new ArrayList<>();

            if (allocationSet.getAllocateTogether()) {
                // Allocate all supplies together
                allocations.addAll(bulkAllocationStrategy.allocate(allocationSet, new ArrayList<>(allocationSet.getSupplies()), skus));
                allocations.addAll(packAllocationStrategy.allocate(allocationSet, new ArrayList<>(allocationSet.getSupplies()), skus));
            } else {
                // Allocate each supply separately
                for (Supply supply : allocationSet.getSupplies()) {
                    if (supply.getItem().getType() == Item.ItemType.BULK) {
                        allocations.addAll(bulkAllocationStrategy.allocate(allocationSet, Collections.singletonList(supply), skus));
                    } else {
                        allocations.addAll(packAllocationStrategy.allocate(allocationSet, Collections.singletonList(supply), skus));
                    }
                }
            }

            // Save allocations
            allocationRepository.saveAll(allocations);

            // Update allocation set status
            allocationSet.setStatus(AllocationSet.Status.COMPLETED);
            allocationSet.setCompletedTime(LocalDateTime.now());
            allocationSetRepository.save(allocationSet);

            // Generate allocation summary using LLM
            AllocationSummary summary = llmService.generateAllocationSummary(allocationSet);
            allocationSummaryRepository.save(summary);

        } catch (Exception e) {
            // Update status to failed
            allocationSet.setStatus(AllocationSet.Status.FAILED);
            allocationSetRepository.save(allocationSet);

            throw e;
        }
    }

    /**
     * Scheduled job to check for and execute allocations that are scheduled
     */
    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void processScheduledAllocations() {
        LocalDateTime now = LocalDateTime.now();
        List<AllocationSet> scheduledSets = allocationSetRepository.findScheduledForExecution(now);

        for (AllocationSet allocationSet : scheduledSets) {
            try {
                executeAllocation(allocationSet.getId());
            } catch (Exception e) {
                // Log error and continue with next allocation set
                // In a real implementation, proper error logging and retry mechanisms would be used
                System.err.println("Error executing allocation set: " + allocationSet.getId() + " - " + e.getMessage());
            }
        }
    }

    // Mapping methods
    private AllocationSetDTO mapToAllocationSetDTO(AllocationSet allocationSet) {
        return AllocationSetDTO.builder()
                .id(allocationSet.getId())
                .name(allocationSet.getName())
                .status(allocationSet.getStatus())
                .needVariableId(allocationSet.getNeedVariable().getId())
                .needVariableName(allocationSet.getNeedVariable().getName())
                .priorityVariableId(allocationSet.getPriorityVariable().getId())
                .priorityVariableName(allocationSet.getPriorityVariable().getName())
                .allocateTogether(allocationSet.getAllocateTogether())
                .supplyIds(allocationSet.getSupplies().stream()
                        .map(Supply::getId)
                        .collect(Collectors.toSet()))
                .destinationIds(allocationSet.getDestinations().stream()
                        .map(Location::getId)
                        .collect(Collectors.toSet()))
                .scheduledTime(allocationSet.getScheduledTime())
                .completedTime(allocationSet.getCompletedTime())
                .createdAt(allocationSet.getCreatedAt())
                .updatedAt(allocationSet.getUpdatedAt())
                .build();
    }

    private AllocationDTO mapToAllocationDTO(Allocation allocation) {
        return AllocationDTO.builder()
                .id(allocation.getId())
                .allocationSetId(allocation.getAllocationSet().getId())
                .supplyId(allocation.getSupply().getId())
                .destinationId(allocation.getDestination().getId())
                .itemStyleColor(allocation.getSupply().getItem().getStyleColor())
                .warehouseName(allocation.getSupply().getWarehouse().getName())
                .destinationName(allocation.getDestination().getName())
                .allocatedQuantity(allocation.getAllocatedQuantity())
                .allocatedPacks(allocation.getAllocatedPacks())
                .transportMode(allocation.getTransportMode())
                .transportCost(allocation.getTransportCost())
                .createdAt(allocation.getCreatedAt())
                .build();
    }

    private AllocationSummaryDTO mapToAllocationSummaryDTO(AllocationSummary summary) {
        return AllocationSummaryDTO.builder()
                .allocationSetId(summary.getId())
                .summarySteps(summary.getSummarySteps())
                .suggestedImprovements(summary.getSuggestedImprovements())
                .createdAt(summary.getCreatedAt())
                .updatedAt(summary.getUpdatedAt())
                .build();
    }
}
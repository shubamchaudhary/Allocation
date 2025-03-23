package com.warehouse.allocation.service.allocation;

import com.warehouse.allocation.model.*;
import com.warehouse.allocation.repository.LocationRepository;
import com.warehouse.allocation.repository.SupplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BulkAllocationStrategy implements AllocationStrategy {

    private final SupplyRepository supplyRepository;
    private final LocationRepository locationRepository;
    private final TransportOptimizationService transportService;

    @Override
    public List<Allocation> allocate(AllocationSet allocationSet, List<Supply> supplies, List<SKU> skus) {
        List<Allocation> allocations = new ArrayList<>();

        // Filter supplies to only include bulk items
        List<Supply> bulkSupplies = supplies.stream()
                .filter(supply -> supply.getItem().getType() == Item.ItemType.BULK)
                .collect(Collectors.toList());

        if (bulkSupplies.isEmpty()) {
            return allocations; // Nothing to allocate
        }

        // Group SKUs by location for easier processing
        Map<UUID, List<SKU>> skusByLocation = skus.stream()
                .collect(Collectors.groupingBy(SKU::getLocationId));

        // First, identify stockout stores and allocate minimum ship quantity
        allocateToStockoutStores(allocationSet, bulkSupplies, skusByLocation, allocations);

        // Then allocate to remaining stores based on priority
        allocateByPriority(allocationSet, bulkSupplies, skusByLocation, allocations);

        return allocations;
    }

    private void allocateToStockoutStores(
            AllocationSet allocationSet,
            List<Supply> supplies,
            Map<UUID, List<SKU>> skusByLocation,
            List<Allocation> allocations) {

        // Find locations that are stocked out (need > 0 but current inventory = 0)
        // For this simplified implementation, we'll assume any location with a high need is stocked out
        List<UUID> stockoutLocationIds = skusByLocation.entrySet().stream()
                .filter(entry -> entry.getValue().stream().anyMatch(sku -> sku.getNeed() > 50))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        for (UUID locationId : stockoutLocationIds) {
            Location location = locationRepository.findById(locationId).orElse(null);
            if (location == null || location.getMinShipQuantity() == null) continue;

            // Get min ship quantity for this location
            int minShipQty = location.getMinShipQuantity();

            // Try to allocate from available supplies
            for (Supply supply : supplies) {
                if (supply.getAvailableQuantity() >= minShipQty) {
                    // Create allocation
                    Allocation allocation = createAllocation(allocationSet, supply, location, minShipQty);
                    allocations.add(allocation);

                    // Update supply quantity
                    supply.setAvailableQuantity(supply.getAvailableQuantity() - minShipQty);

                    // We've allocated to this location, so break inner loop
                    break;
                }
            }
        }

        // Update supplies in database
        supplyRepository.saveAll(supplies);
    }

    private void allocateByPriority(
            AllocationSet allocationSet,
            List<Supply> supplies,
            Map<UUID, List<SKU>> skusByLocation,
            List<Allocation> allocations) {

        // Group locations by priority
        Map<Integer, List<UUID>> locationsByPriority = new HashMap<>();

        skusByLocation.forEach((locationId, locationSkus) -> {
            // Get the highest priority for this location
            OptionalInt highestPriority = locationSkus.stream()
                    .mapToInt(SKU::getPriority)
                    .max();

            if (highestPriority.isPresent()) {
                int priority = highestPriority.getAsInt();
                locationsByPriority.computeIfAbsent(priority, k -> new ArrayList<>()).add(locationId);
            }
        });

        // Sort priorities in descending order (highest first)
        List<Integer> prioritiesSorted = locationsByPriority.keySet().stream()
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        // Allocate by priority
        for (Integer priority : prioritiesSorted) {
            List<UUID> locationsWithPriority = locationsByPriority.get(priority);

            if (locationsWithPriority.size() == 1) {
                // Only one location with this priority, allocate as much as needed
                UUID locationId = locationsWithPriority.get(0);
                allocateToSingleLocation(allocationSet, supplies, skusByLocation.get(locationId), locationId, allocations);
            } else {
                // Multiple locations with same priority, do fair share allocation
                allocateFairShare(allocationSet, supplies, skusByLocation, locationsWithPriority, allocations);
            }
        }

        // Update supplies in database
        supplyRepository.saveAll(supplies);
    }

    private void allocateToSingleLocation(
            AllocationSet allocationSet,
            List<Supply> supplies,
            List<SKU> locationSkus,
            UUID locationId,
            List<Allocation> allocations) {

        Location location = locationRepository.findById(locationId).orElse(null);
        if (location == null) return;

        // Calculate total need for this location across all SKUs
        double totalNeed = locationSkus.stream()
                .mapToDouble(SKU::getNeed)
                .sum();

        int needInteger = (int) Math.ceil(totalNeed);

        // Allocate from supplies
        for (Supply supply : supplies) {
            if (supply.getAvailableQuantity() <= 0) continue;

            int toAllocate = Math.min(needInteger, supply.getAvailableQuantity());
            if (toAllocate > 0) {
                // Create allocation
                Allocation allocation = createAllocation(allocationSet, supply, location, toAllocate);
                allocations.add(allocation);

                // Update supply and need
                supply.setAvailableQuantity(supply.getAvailableQuantity() - toAllocate);
                needInteger -= toAllocate;

                if (needInteger <= 0) break;
            }
        }
    }

    private void allocateFairShare(
            AllocationSet allocationSet,
            List<Supply> supplies,
            Map<UUID, List<SKU>> skusByLocation,
            List<UUID> locationIds,
            List<Allocation> allocations) {

        // Calculate total need across all locations
        double totalNeed = 0;
        Map<UUID, Double> needByLocation = new HashMap<>();

        for (UUID locationId : locationIds) {
            List<SKU> locationSkus = skusByLocation.get(locationId);
            if (locationSkus != null) {
                double locationNeed = locationSkus.stream()
                        .mapToDouble(SKU::getNeed)
                        .sum();

                needByLocation.put(locationId, locationNeed);
                totalNeed += locationNeed;
            }
        }

        // Calculate total available supply
        int totalAvailableSupply = supplies.stream()
                .mapToInt(Supply::getAvailableQuantity)
                .sum();

        // If supply exceeds need, allocate fully
        if (totalAvailableSupply >= totalNeed) {
            for (UUID locationId : locationIds) {
                Location location = locationRepository.findById(locationId).orElse(null);
                if (location == null) continue;

                double locationNeed = needByLocation.getOrDefault(locationId, 0.0);
                int needInteger = (int) Math.ceil(locationNeed);

                for (Supply supply : supplies) {
                    if (supply.getAvailableQuantity() <= 0) continue;

                    int toAllocate = Math.min(needInteger, supply.getAvailableQuantity());
                    if (toAllocate > 0) {
                        // Create allocation
                        Allocation allocation = createAllocation(allocationSet, supply, location, toAllocate);
                        allocations.add(allocation);

                        // Update supply and need
                        supply.setAvailableQuantity(supply.getAvailableQuantity() - toAllocate);
                        needInteger -= toAllocate;

                        if (needInteger <= 0) break;
                    }
                }
            }
        } else {
            // Fair share allocation based on need proportion
            for (UUID locationId : locationIds) {
                Location location = locationRepository.findById(locationId).orElse(null);
                if (location == null) continue;

                double locationNeed = needByLocation.getOrDefault(locationId, 0.0);
                double proportion = locationNeed / totalNeed;
                int fairShare = (int) Math.ceil(totalAvailableSupply * proportion);

                for (Supply supply : supplies) {
                    if (supply.getAvailableQuantity() <= 0) continue;

                    int toAllocate = Math.min(fairShare, supply.getAvailableQuantity());
                    if (toAllocate > 0) {
                        // Create allocation
                        Allocation allocation = createAllocation(allocationSet, supply, location, toAllocate);
                        allocations.add(allocation);

                        // Update supply and need
                        supply.setAvailableQuantity(supply.getAvailableQuantity() - toAllocate);
                        fairShare -= toAllocate;

                        if (fairShare <= 0) break;
                    }
                }
            }
        }
    }

    private Allocation createAllocation(AllocationSet allocationSet, Supply supply, Location destination, int quantity) {
        // Determine optimal transport mode
        TransportOptimizationService.TransportOption transportOption =
                transportService.findOptimalTransport(supply.getWarehouse(), destination, quantity);

        return Allocation.builder()
                .allocationSet(allocationSet)
                .supply(supply)
                .destination(destination)
                .allocatedQuantity(quantity)
                .allocatedPacks(null) // Not applicable for bulk
                .transportMode(transportOption.getTransportMode())
                .transportCost(transportOption.getCost())
                .build();
    }
}
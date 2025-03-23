package com.warehouse.allocation.service.need;

import com.warehouse.allocation.model.Item;
import com.warehouse.allocation.model.Location;
import com.warehouse.allocation.model.NeedVariable;
import com.warehouse.allocation.model.SKU;
import com.warehouse.allocation.repository.ItemRepository;
import com.warehouse.allocation.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NeedService {

    private final ItemRepository itemRepository;
    private final LocationRepository locationRepository;

    /**
     * Calculate need for a combination of items and locations based on a need variable
     *
     * @param itemIds List of item IDs
     * @param locationIds List of location IDs
     * @param needVariable Need variable to use for calculation
     * @return List of SKUs with calculated needs
     */
    public List<SKU> calculateNeed(List<UUID> itemIds, List<UUID> locationIds, NeedVariable needVariable) {
        List<SKU> skus = new ArrayList<>();

        for (UUID itemId : itemIds) {
            Item item = itemRepository.findById(itemId).orElse(null);
            if (item == null) continue;

            for (UUID locationId : locationIds) {
                Location location = locationRepository.findById(locationId).orElse(null);
                if (location == null) continue;

                // Here we would call the actual need calculation logic based on the variable
                // For now, we'll use a placeholder calculation
                double need = calculatePlaceholderNeed(item, location, needVariable);

                SKU sku = SKU.builder()
                        .itemId(itemId)
                        .locationId(locationId)
                        .need(need)
                        .priority(0) // Initial priority is 0
                        .hierarchyLevel("STYLECOLOR") // Default level
                        .build();

                skus.add(sku);
            }
        }

        return skus;
    }

    /**
     * Placeholder method for need calculation - in a real implementation, this would use
     * sophisticated algorithms based on sales data, forecasts, etc.
     */
    private double calculatePlaceholderNeed(Item item, Location location, NeedVariable needVariable) {
        // This is a simplified placeholder logic. In reality, this would be much more complex
        // and would take into account sales history, seasonality, trends, etc.

        // For demonstration purposes, we'll create a random need between 10 and 100
        return Math.max(10, Math.random() * 100);
    }
}
package com.warehouse.allocation.service.allocation;

import com.warehouse.allocation.model.Allocation;
import com.warehouse.allocation.model.Location;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service to optimize transport selection based on cost, speed, and other factors
 */
@Service
@RequiredArgsConstructor
public class TransportOptimizationService {

    /**
     * Find the optimal transport method for a given source-destination pair and quantity
     *
     * @param source Source location
     * @param destination Destination location
     * @param quantity Quantity to transport
     * @return TransportOption containing the selected mode and estimated cost
     */
    public TransportOption findOptimalTransport(Location source, Location destination, int quantity) {
        // Calculate distance between locations using their coordinates
        double distance = calculateDistance(source, destination);

        // Determine the transport mode based on distance, quantity, and other factors
        // This is a simplified implementation - a real one would be much more complex
        Allocation.TransportMode transportMode;
        double cost;

        if (distance < 50) {
            // Short distance - use truck
            transportMode = Allocation.TransportMode.TRUCK;
            cost = calculateTruckCost(distance, quantity);
        } else if (distance < 500) {
            // Medium distance - compare truck vs train
            double truckCost = calculateTruckCost(distance, quantity);
            double trainCost = calculateTrainCost(distance, quantity);

            if (truckCost <= trainCost) {
                transportMode = Allocation.TransportMode.TRUCK;
                cost = truckCost;
            } else {
                transportMode = Allocation.TransportMode.TRAIN;
                cost = trainCost;
            }
        } else {
            // Long distance - compare train vs ship
            double trainCost = calculateTrainCost(distance, quantity);
            double shipCost = calculateShipCost(distance, quantity);

            if (trainCost <= shipCost) {
                transportMode = Allocation.TransportMode.TRAIN;
                cost = trainCost;
            } else {
                transportMode = Allocation.TransportMode.SHIP;
                cost = shipCost;
            }
        }

        // Special case for API (third-party logistics) - might be cheaper for certain routes or quantities
        double apiCost = calculateApiCost(distance, quantity);
        if (apiCost < cost) {
            transportMode = Allocation.TransportMode.API;
            cost = apiCost;
        }

        return new TransportOption(transportMode, cost);
    }

    private double calculateDistance(Location source, Location destination) {
        if (source.getLatitude() == null || source.getLongitude() == null ||
                destination.getLatitude() == null || destination.getLongitude() == null) {
            // If coordinates are missing, use a default distance estimate
            return 100.0;
        }

        // Calculate the distance using the Haversine formula
        double earthRadius = 6371; // kilometers

        double latDistance = Math.toRadians(destination.getLatitude() - source.getLatitude());
        double lonDistance = Math.toRadians(destination.getLongitude() - source.getLongitude());

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(source.getLatitude())) * Math.cos(Math.toRadians(destination.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }

    private double calculateTruckCost(double distance, int quantity) {
        // Base cost + distance cost + quantity cost
        return 50 + (0.5 * distance) + (0.1 * quantity);
    }

    private double calculateTrainCost(double distance, int quantity) {
        // Higher base cost, but lower distance and quantity costs
        return 100 + (0.3 * distance) + (0.05 * quantity);
    }

    private double calculateShipCost(double distance, int quantity) {
        // High base cost, but much lower distance cost
        return 500 + (0.1 * distance) + (0.02 * quantity);
    }

    private double calculateApiCost(double distance, int quantity) {
        // Third-party logistics - can be efficient for certain distances and quantities
        return 75 + (0.4 * distance) + (0.08 * quantity);
    }

    @Data
    @Builder
    public static class TransportOption {
        private final Allocation.TransportMode transportMode;
        private final double cost;
    }
}
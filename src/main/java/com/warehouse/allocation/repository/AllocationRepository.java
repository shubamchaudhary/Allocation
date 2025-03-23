package com.warehouse.allocation.repository;

import com.warehouse.allocation.model.Allocation;
import com.warehouse.allocation.model.AllocationSet;
import com.warehouse.allocation.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AllocationRepository extends JpaRepository<Allocation, UUID> {
    List<Allocation> findByAllocationSet(AllocationSet allocationSet);

    List<Allocation> findByDestination(Location destination);

    List<Allocation> findByTransportMode(Allocation.TransportMode transportMode);

    @Query("SELECT SUM(a.allocatedQuantity) FROM Allocation a WHERE a.allocationSet.id = :allocationSetId")
    Integer getTotalAllocatedQuantity(@Param("allocationSetId") UUID allocationSetId);

    @Query("SELECT a.transportMode, SUM(a.transportCost) as totalCost FROM Allocation a " +
            "WHERE a.allocationSet.id = :allocationSetId GROUP BY a.transportMode")
    List<Object[]> getTransportCostSummary(@Param("allocationSetId") UUID allocationSetId);
}
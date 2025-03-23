package com.warehouse.allocation.service.allocation;

import com.warehouse.allocation.model.Allocation;
import com.warehouse.allocation.model.AllocationSet;
import com.warehouse.allocation.model.SKU;
import com.warehouse.allocation.model.Supply;

import java.util.List;

/**
 * Interface for allocation strategies
 */
public interface AllocationStrategy {

    /**
     * Perform allocation based on the given parameters
     *
     * @param allocationSet Allocation set
     * @param supplies List of supplies
     * @param skus List of SKUs with need and priority information
     * @return List of allocations
     */
    List<Allocation> allocate(AllocationSet allocationSet, List<Supply> supplies, List<SKU> skus);
}
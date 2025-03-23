package com.warehouse.allocation.repository;

import com.warehouse.allocation.model.AllocationSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AllocationSummaryRepository extends JpaRepository<AllocationSummary, UUID> {
    // Basic methods provided by JpaRepository are sufficient
}
package com.warehouse.allocation.repository;

import com.warehouse.allocation.model.AllocationSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AllocationSetRepository extends JpaRepository<AllocationSet, UUID> {
    List<AllocationSet> findByStatusOrderByCreatedAtDesc(AllocationSet.Status status);

    @Query("SELECT a FROM AllocationSet a WHERE a.status = 'SCHEDULED' AND a.scheduledTime <= :now")
    List<AllocationSet> findScheduledForExecution(@Param("now") LocalDateTime now);

    List<AllocationSet> findBySupplies_ItemId(UUID itemId);

    List<AllocationSet> findTop10ByOrderByCreatedAtDesc();
}
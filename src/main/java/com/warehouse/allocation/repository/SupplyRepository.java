package com.warehouse.allocation.repository;

import com.warehouse.allocation.model.Item;
import com.warehouse.allocation.model.Location;
import com.warehouse.allocation.model.Supply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SupplyRepository extends JpaRepository<Supply, UUID> {
    List<Supply> findByItem(Item item);

    List<Supply> findByWarehouse(Location warehouse);

    List<Supply> findByAvailableQuantityGreaterThan(Integer minQuantity);

    @Query("SELECT s FROM Supply s WHERE s.availableFrom <= :now AND s.availableQuantity > 0")
    List<Supply> findAvailableSupplies(@Param("now") LocalDateTime now);

    @Query("SELECT s FROM Supply s WHERE s.item.id = :itemId AND s.warehouse.id = :warehouseId")
    List<Supply> findByItemAndWarehouse(@Param("itemId") UUID itemId, @Param("warehouseId") UUID warehouseId);
}
package com.warehouse.allocation.repository;

import com.warehouse.allocation.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {
    Optional<Location> findByCode(String code);

    List<Location> findByType(Location.LocationType type);

    @Query("SELECT l FROM Location l WHERE l.type = 'STORE' AND l.minShipQuantity <= :quantity")
    List<Location> findStoresWithMinShipQuantity(@Param("quantity") Integer quantity);
}
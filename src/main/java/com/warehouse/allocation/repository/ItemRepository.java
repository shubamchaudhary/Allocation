package com.warehouse.allocation.repository;

import com.warehouse.allocation.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ItemRepository extends JpaRepository<Item, UUID> {
    Optional<Item> findByStyleColor(String styleColor);

    List<Item> findByStyle(String style);

    List<Item> findByItemClass(String itemClass);

    @Query("SELECT i FROM Item i WHERE i.type = :type")
    List<Item> findByType(@Param("type") Item.ItemType type);
}
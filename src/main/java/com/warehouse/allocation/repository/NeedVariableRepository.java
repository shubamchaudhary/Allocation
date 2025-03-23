package com.warehouse.allocation.repository;

import com.warehouse.allocation.model.NeedVariable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NeedVariableRepository extends JpaRepository<NeedVariable, UUID> {
    Optional<NeedVariable> findByName(String name);
}
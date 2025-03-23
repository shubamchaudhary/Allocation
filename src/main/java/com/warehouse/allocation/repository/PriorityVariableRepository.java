package com.warehouse.allocation.repository;

import com.warehouse.allocation.model.PriorityVariable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PriorityVariableRepository extends JpaRepository<PriorityVariable, UUID> {
    Optional<PriorityVariable> findByName(String name);
}
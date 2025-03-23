package com.warehouse.allocation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "allocation_set")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AllocationSet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "need_variable_id")
    private NeedVariable needVariable;

    @ManyToOne
    @JoinColumn(name = "priority_variable_id")
    private PriorityVariable priorityVariable;

    @Column
    private Boolean allocateTogether;

    @ManyToMany
    @JoinTable(
            name = "allocation_set_supplies",
            joinColumns = @JoinColumn(name = "allocation_set_id"),
            inverseJoinColumns = @JoinColumn(name = "supply_id")
    )
    private Set<Supply> supplies = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "allocation_set_destinations",
            joinColumns = @JoinColumn(name = "allocation_set_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    private Set<Location> destinations = new HashSet<>();

    @OneToMany(mappedBy = "allocationSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Allocation> allocations = new HashSet<>();

    @Column
    private LocalDateTime scheduledTime;

    @Column
    private LocalDateTime completedTime;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public enum Status {
        DRAFT, SCHEDULED, IN_PROGRESS, COMPLETED, FAILED
    }
}

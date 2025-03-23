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
import java.util.UUID;

@Entity
@Table(name = "allocation_summary")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AllocationSummary {

    @Id
    @Column(name = "allocation_set_id")
    private UUID id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "allocation_set_id")
    private AllocationSet allocationSet;

    @Column(columnDefinition = "jsonb", nullable = false)
    private String summarySteps;

    @Column(columnDefinition = "jsonb", nullable = false)
    private String suggestedImprovements;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
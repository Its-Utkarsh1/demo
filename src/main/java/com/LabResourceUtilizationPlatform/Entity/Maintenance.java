package com.LabResourceUtilizationPlatform.Entity;

import com.LabResourceUtilizationPlatform.Entity.Enum.MaintenanceStatus;
import com.LabResourceUtilizationPlatform.Entity.Enum.MaintenanceType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id", nullable = false)
    private User technician;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceType maintenanceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceStatus status;

    @Column(nullable = false)
    private LocalDate scheduledDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
package com.LabResourceUtilizationPlatform.Entity;

import com.LabResourceUtilizationPlatform.Entity.Enum.EquipmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "equipment",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"equipment_code", "lab_id"})
        },
        indexes = {
                @Index(name = "idx_equipment_lab", columnList = "lab_id"),
                @Index(name = "idx_equipment_code", columnList = "equipment_code")
        }
)
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String equipmentName;

    @Column(name = "equipment_code", nullable = false)
    private String equipmentCode;

    @Column(nullable = false)
    private String manufacturer;

    @Column(nullable = false)
    private String model;

    @Column(length = 2000)
    private String description;

    @Column(length = 5000)
    private String specifications;

    @Column
    private String imageUrl;

    @Column
    private LocalDate purchaseDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id", nullable = false)
    private Lab lab;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
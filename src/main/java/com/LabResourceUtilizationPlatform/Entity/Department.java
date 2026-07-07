package com.LabResourceUtilizationPlatform.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "departments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "institution_id"})
        }
)@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;
}
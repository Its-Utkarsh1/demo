package com.LabResourceUtilizationPlatform.Entity;

import com.LabResourceUtilizationPlatform.Entity.Enum.LabStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@Table(
        name = "labs",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "lab_code",
                                "institution_id",
                                "department_id"
                        }
                ),
                @UniqueConstraint(
                        columnNames = {
                                "lab_name",
                                "institution_id",
                                "department_id"
                        }
                )
        }
)@AllArgsConstructor
@NoArgsConstructor
public class Lab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String labName;

    @Column(nullable = false)
    private String labCode;

    @Column(nullable = false)
    private String location;

    @Min(value = 1, message = "User capacity must be at least 1")
    private Integer usercapacity;

    @Enumerated(EnumType.STRING)
    private LabStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution institution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}

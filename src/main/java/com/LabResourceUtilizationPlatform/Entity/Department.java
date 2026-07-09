package com.LabResourceUtilizationPlatform.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "departments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "institution_id"})
        }
)
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id", nullable = false)
    private Institution institution;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<User> users;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL)
    private List<Lab> labs;
}
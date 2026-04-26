package com.example.petcaresystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "grooming_records")
@Getter @Setter @NoArgsConstructor
public class GroomingRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String serviceSummary;

    @Column(length=2000)
    private String notes;

    private String groomerName;
    private Double price;
    private Integer durationMinutes;
    private LocalDateTime performedAt;
    private LocalDateTime nextSuggestedDate;

    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable=false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable=false)
    private User owner;

    @PrePersist
    protected void onCreate() {
        if (performedAt == null) performedAt = LocalDateTime.now();
        if (isActive == null) isActive = true;
    }
}

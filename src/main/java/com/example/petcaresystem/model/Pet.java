package com.example.petcaresystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String species;

    private String breed;
    private Integer age;
    private LocalDate dateOfBirth;
    private String gender;
    private Double weight;
    private String color;

    @Column(length = 2000)
    private String medicalNotes;

    private String imageUrl;
    private String imageName;
    private String microchipId;

    @Column(nullable = false)
    private Boolean isActive = true;

    // ✅ Correct owner mapping
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnoreProperties({"pets", "hibernateLazyInitializer", "handler"})
    private User owner;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void calculateAge() {
        if (this.dateOfBirth != null) {
            this.age = java.time.Period.between(this.dateOfBirth, LocalDate.now()).getYears();
        }
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", species='" + species + '\'' +
                ", breed='" + breed + '\'' +
                ", ownerId=" + (owner != null ? owner.getId() : null) +
                '}';
    }
}

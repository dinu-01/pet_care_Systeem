package com.example.petcaresystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_reports")
public class MedicalReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Report code is required")
    @Column(name = "report_code", nullable = false, unique = true)
    private String reportCode;

    @NotBlank(message = "Diagnosis is required")
    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "symptoms", columnDefinition = "TEXT")
    private String symptoms;

    @Column(name = "treatment", columnDefinition = "TEXT")
    private String treatment;

    @Column(name = "medications", columnDefinition = "TEXT")
    private String medications;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @NotNull(message = "Report date is required")
    @Column(name = "report_date", nullable = false)
    private LocalDateTime reportDate;

    @Column(name = "next_visit_date")
    private LocalDateTime nextVisitDate;

    @Column(name = "veterinarian_name")
    private String veterinarianName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id")
    private User veterinarian;

    // Constructors
    public MedicalReport() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public MedicalReport(String reportCode, String diagnosis, Pet pet, User veterinarian) {
        this();
        this.reportCode = reportCode;
        this.diagnosis = diagnosis;
        this.pet = pet;
        this.veterinarian = veterinarian;
        this.reportDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReportCode() { return reportCode; }
    public void setReportCode(String reportCode) { this.reportCode = reportCode; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }

    public String getMedications() { return medications; }
    public void setMedications(String medications) { this.medications = medications; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getReportDate() { return reportDate; }
    public void setReportDate(LocalDateTime reportDate) { this.reportDate = reportDate; }

    public LocalDateTime getNextVisitDate() { return nextVisitDate; }
    public void setNextVisitDate(LocalDateTime nextVisitDate) { this.nextVisitDate = nextVisitDate; }

    public String getVeterinarianName() { return veterinarianName; }
    public void setVeterinarianName(String veterinarianName) { this.veterinarianName = veterinarianName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }

    public User getVeterinarian() { return veterinarian; }
    public void setVeterinarian(User veterinarian) { this.veterinarian = veterinarian; }

    // JPA Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();

        // Generate report code if not provided
        if (this.reportCode == null || this.reportCode.trim().isEmpty()) {
            this.reportCode = "MR-" + System.currentTimeMillis();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "MedicalReport{" +
                "id=" + id +
                ", reportCode='" + reportCode + '\'' +
                ", diagnosis='" + diagnosis + '\'' +
                ", pet=" + (pet != null ? pet.getName() : "null") +
                '}';
    }
}
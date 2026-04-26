package com.example.petcaresystem.dto;

import java.time.LocalDateTime;

public class MedicalReportDTO {
    private Long id;
    private String reportCode;
    private String diagnosis;
    private String symptoms;
    private String treatment;
    private String medications;
    private String notes;
    private LocalDateTime reportDate;
    private LocalDateTime nextVisitDate;
    private String veterinarianName;
    private Long petId;
    private String petName;
    private String ownerName;

    public MedicalReportDTO() {}

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
    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}

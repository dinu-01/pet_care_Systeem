package com.example.petcaresystem.service;

import com.example.petcaresystem.model.MedicalReport;
import java.util.List;

public interface MedicalReportService {

    MedicalReport createMedicalReport(MedicalReport medicalReport, Long petId, Long veterinarianId);

    List<MedicalReport> getAllMedicalReports();

    MedicalReport getMedicalReportById(Long id);

    MedicalReport getMedicalReportByCode(String reportCode);

    List<MedicalReport> getMedicalReportsByPet(Long petId);

    List<MedicalReport> getMedicalReportsByVeterinarian(Long veterinarianId);

    List<MedicalReport> getMedicalReportsByPetOwner(Long ownerId);

    MedicalReport updateMedicalReport(Long id, MedicalReport medicalReportDetails);

    void deleteMedicalReport(Long id);

    boolean existsByReportCode(String reportCode);

    // ✅ NEW: Used for real-time dashboard counts
    long countMedicalReports();
}
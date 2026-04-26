package com.example.petcaresystem.service;

import com.example.petcaresystem.model.MedicalReport;
import com.example.petcaresystem.model.Pet;
import com.example.petcaresystem.model.User;
import com.example.petcaresystem.repo.MedicalReportRepository;
import com.example.petcaresystem.repo.PetRepository;
import com.example.petcaresystem.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MedicalReportServiceImpl implements MedicalReportService {

    private final MedicalReportRepository medicalReportRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Override
    public MedicalReport createMedicalReport(MedicalReport medicalReport, Long petId, Long veterinarianId) {
        try {
            log.info("Creating medical report for pet ID: {} by veterinarian ID: {}", petId, veterinarianId);

            // ✅ 1. Find the pet
            Pet pet = petRepository.findById(petId)
                    .orElseThrow(() -> new RuntimeException("Pet not found with id: " + petId));

            // ✅ 2. Find the veterinarian
            User veterinarian = userRepository.findById(veterinarianId)
                    .orElseThrow(() -> new RuntimeException("Veterinarian not found with id: " + veterinarianId));

            // ✅ 3. Role validation
            if (!"VETERINARIAN".equalsIgnoreCase(veterinarian.getRole()) &&
                    !"ADMIN".equalsIgnoreCase(veterinarian.getRole())) {
                throw new RuntimeException("User is not authorized to create medical reports");
            }

            // ✅ 4. Automatically set report date if not provided
            if (medicalReport.getReportDate() == null) {
                medicalReport.setReportDate(LocalDateTime.now());
            }

            // ✅ 5. Check duplicate report code
            if (medicalReport.getReportCode() != null &&
                    medicalReportRepository.existsByReportCode(medicalReport.getReportCode())) {
                throw new RuntimeException("Report code '" + medicalReport.getReportCode() + "' already exists");
            }

            // ✅ 6. Set relationships
            medicalReport.setPet(pet);
            medicalReport.setVeterinarian(veterinarian);
            medicalReport.setVeterinarianName(veterinarian.getUsername());

            // ✅ 7. Save the record
            MedicalReport savedReport = medicalReportRepository.save(medicalReport);
            log.info("✅ Medical report created successfully with ID: {}", savedReport.getId());
            return savedReport;

        } catch (Exception e) {
            log.error("❌ Error creating medical report: {}", e.getMessage());
            throw new RuntimeException("Error creating medical report: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalReport> getAllMedicalReports() {
        try {
            log.info("Retrieving all medical reports");
            return medicalReportRepository.findAllWithDetails();
        } catch (Exception e) {
            log.error("Error retrieving medical reports: {}", e.getMessage());
            throw new RuntimeException("Error retrieving medical reports: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalReport getMedicalReportById(Long id) {
        try {
            log.info("Retrieving medical report by ID: {}", id);
            Optional<MedicalReport> report = medicalReportRepository.findByIdWithDetails(id);
            return report.orElseThrow(() -> new RuntimeException("Medical report not found with ID: " + id));
        } catch (Exception e) {
            log.error("Error retrieving medical report by id {}: {}", id, e.getMessage());
            throw new RuntimeException("Error retrieving medical report: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalReport getMedicalReportByCode(String reportCode) {
        try {
            log.info("Retrieving medical report by code: {}", reportCode);
            return medicalReportRepository.findByReportCode(reportCode)
                    .orElseThrow(() -> new RuntimeException("Medical report not found with code: " + reportCode));
        } catch (Exception e) {
            log.error("Error retrieving medical report by code {}: {}", reportCode, e.getMessage());
            throw new RuntimeException("Error retrieving medical report: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalReport> getMedicalReportsByPet(Long petId) {
        try {
            log.info("Retrieving medical reports for pet ID: {}", petId);
            return medicalReportRepository.findByPetId(petId);
        } catch (Exception e) {
            log.error("Error retrieving medical reports for pet {}: {}", petId, e.getMessage());
            throw new RuntimeException("Error retrieving medical reports: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalReport> getMedicalReportsByVeterinarian(Long veterinarianId) {
        try {
            log.info("Retrieving medical reports for veterinarian ID: {}", veterinarianId);
            return medicalReportRepository.findByVeterinarianId(veterinarianId);
        } catch (Exception e) {
            log.error("Error retrieving medical reports for veterinarian {}: {}", veterinarianId, e.getMessage());
            throw new RuntimeException("Error retrieving medical reports: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalReport> getMedicalReportsByPetOwner(Long ownerId) {
        try {
            log.info("Retrieving medical reports for pet owner ID: {}", ownerId);
            return medicalReportRepository.findByPetOwnerId(ownerId);
        } catch (Exception e) {
            log.error("Error retrieving medical reports for owner {}: {}", ownerId, e.getMessage());
            throw new RuntimeException("Error retrieving medical reports: " + e.getMessage());
        }
    }

    @Override
    public MedicalReport updateMedicalReport(Long id, MedicalReport medicalReportDetails) {
        try {
            log.info("Updating medical report with ID: {}", id);
            MedicalReport report = medicalReportRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Medical report not found with id: " + id));

            // ✅ Selective field updates
            if (medicalReportDetails.getDiagnosis() != null)
                report.setDiagnosis(medicalReportDetails.getDiagnosis());
            if (medicalReportDetails.getSymptoms() != null)
                report.setSymptoms(medicalReportDetails.getSymptoms());
            if (medicalReportDetails.getTreatment() != null)
                report.setTreatment(medicalReportDetails.getTreatment());
            if (medicalReportDetails.getMedications() != null)
                report.setMedications(medicalReportDetails.getMedications());
            if (medicalReportDetails.getNotes() != null)
                report.setNotes(medicalReportDetails.getNotes());
            if (medicalReportDetails.getNextVisitDate() != null)
                report.setNextVisitDate(medicalReportDetails.getNextVisitDate());

            report.setUpdatedAt(LocalDateTime.now());

            MedicalReport updatedReport = medicalReportRepository.save(report);
            log.info("✅ Medical report updated successfully: {}", updatedReport.getId());
            return updatedReport;

        } catch (Exception e) {
            log.error("Error updating medical report {}: {}", id, e.getMessage());
            throw new RuntimeException("Error updating medical report: " + e.getMessage());
        }
    }

    @Override
    public void deleteMedicalReport(Long id) {
        try {
            log.info("Deleting medical report with ID: {}", id);
            if (!medicalReportRepository.existsById(id)) {
                throw new RuntimeException("Medical report not found with id: " + id);
            }
            medicalReportRepository.deleteById(id);
            log.info("✅ Medical report deleted successfully: {}", id);
        } catch (Exception e) {
            log.error("Error deleting medical report {}: {}", id, e.getMessage());
            throw new RuntimeException("Error deleting medical report: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByReportCode(String reportCode) {
        try {
            return medicalReportRepository.existsByReportCode(reportCode);
        } catch (Exception e) {
            log.error("Error checking report code existence {}: {}", reportCode, e.getMessage());
            throw new RuntimeException("Error checking report code existence: " + e.getMessage());
        }
    }

    // ✅ NEW: Real-time dashboard count support
    @Override
    @Transactional(readOnly = true)
    public long countMedicalReports() {
        try {
            log.info("Counting total medical reports...");
            return medicalReportRepository.count();
        } catch (Exception e) {
            log.error("Error counting medical reports: {}", e.getMessage());
            throw new RuntimeException("Error counting medical reports: " + e.getMessage());
        }
    }
}

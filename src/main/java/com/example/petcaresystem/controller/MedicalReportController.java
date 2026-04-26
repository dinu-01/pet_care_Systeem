package com.example.petcaresystem.controller;

import com.example.petcaresystem.dto.MedicalReportDTO;
import com.example.petcaresystem.model.MedicalReport;
import com.example.petcaresystem.model.User;
import com.example.petcaresystem.service.MedicalReportService;
import com.example.petcaresystem.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/medical-reports")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MedicalReportController {

    private final MedicalReportService medicalReportService;
    private final UserRepository userRepository;

    // ✅ Health Check
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("✅ Medical Reports API is healthy!");
    }

    // ✅ Convert Entity → DTO
    private MedicalReportDTO convertToDTO(MedicalReport report) {
        MedicalReportDTO dto = new MedicalReportDTO();
        dto.setId(report.getId());
        dto.setReportCode(report.getReportCode());
        dto.setDiagnosis(report.getDiagnosis());
        dto.setSymptoms(report.getSymptoms());
        dto.setTreatment(report.getTreatment());
        dto.setMedications(report.getMedications());
        dto.setNotes(report.getNotes());
        dto.setReportDate(report.getReportDate());
        dto.setNextVisitDate(report.getNextVisitDate());
        dto.setPetId(report.getPet().getId());
        dto.setPetName(report.getPet().getName());
        dto.setOwnerName(report.getPet().getOwner().getUsername());
        dto.setVeterinarianName(report.getVeterinarianName());
        return dto;
    }

    // ✅ Create Medical Report (Vet or Admin only)
    @PostMapping("/create/{petId}/{veterinarianId}")
    public ResponseEntity<?> createReport(
            @PathVariable Long petId,
            @PathVariable Long veterinarianId,
            @RequestBody MedicalReport medicalReport) {

        User vet = userRepository.findById(veterinarianId)
                .orElseThrow(() -> new RuntimeException("Veterinarian not found"));

        if (!"VETERINARIAN".equalsIgnoreCase(vet.getRole()) &&
                !"ADMIN".equalsIgnoreCase(vet.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("❌ Only veterinarians or admins can create medical reports");
        }

        MedicalReport createdReport = medicalReportService.createMedicalReport(medicalReport, petId, veterinarianId);
        return new ResponseEntity<>(convertToDTO(createdReport), HttpStatus.CREATED);
    }

    // ✅ Get All Reports
    @GetMapping
    public ResponseEntity<List<MedicalReportDTO>> getAllReports() {
        List<MedicalReportDTO> reports = medicalReportService.getAllMedicalReports()
                .stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(reports);
    }

    // ✅ Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<MedicalReportDTO> getById(@PathVariable Long id) {
        MedicalReport report = medicalReportService.getMedicalReportById(id);
        return ResponseEntity.ok(convertToDTO(report));
    }

    // ✅ Get by Pet
    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<MedicalReportDTO>> getByPet(@PathVariable Long petId) {
        List<MedicalReportDTO> reports = medicalReportService.getMedicalReportsByPet(petId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(reports);
    }

    // ✅ Get by Pet Owner
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<MedicalReportDTO>> getByOwner(@PathVariable Long ownerId) {
        List<MedicalReportDTO> reports = medicalReportService.getMedicalReportsByPetOwner(ownerId)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(reports);
    }

    // ✅ Update Medical Report (Vet or Admin only)
    @PutMapping("/update/{id}/{veterinarianId}")
    public ResponseEntity<?> updateReport(
            @PathVariable Long id,
            @PathVariable Long veterinarianId,
            @RequestBody MedicalReport medicalReport) {

        User vet = userRepository.findById(veterinarianId)
                .orElseThrow(() -> new RuntimeException("Veterinarian not found"));

        if (!"VETERINARIAN".equalsIgnoreCase(vet.getRole()) &&
                !"ADMIN".equalsIgnoreCase(vet.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("❌ Only veterinarians or admins can update medical reports");
        }

        MedicalReport updatedReport = medicalReportService.updateMedicalReport(id, medicalReport);
        return ResponseEntity.ok(convertToDTO(updatedReport));
    }

    // ✅ Delete Medical Report (Vet or Admin only)
    @DeleteMapping("/delete/{id}/{veterinarianId}")
    public ResponseEntity<?> deleteReport(
            @PathVariable Long id,
            @PathVariable Long veterinarianId) {

        User vet = userRepository.findById(veterinarianId)
                .orElseThrow(() -> new RuntimeException("Veterinarian not found"));

        if (!"VETERINARIAN".equalsIgnoreCase(vet.getRole()) &&
                !"ADMIN".equalsIgnoreCase(vet.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("❌ Only veterinarians or admins can delete medical reports");
        }

        medicalReportService.deleteMedicalReport(id);
        return ResponseEntity.ok("✅ Medical report deleted successfully");
    }

    // ✅ Total Count Endpoint (for live dashboard updates)
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalMedicalReports() {
        long count = medicalReportService.countMedicalReports();
        return ResponseEntity.ok(Map.of("count", count));
    }
}
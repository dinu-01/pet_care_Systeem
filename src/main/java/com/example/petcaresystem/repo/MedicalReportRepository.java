package com.example.petcaresystem.repo;

import com.example.petcaresystem.model.MedicalReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalReportRepository extends JpaRepository<MedicalReport, Long> {

    // Find by report code
    Optional<MedicalReport> findByReportCode(String reportCode);

    // Find all reports for a specific pet
    List<MedicalReport> findByPetId(Long petId);

    // Find all reports by veterinarian
    List<MedicalReport> findByVeterinarianId(Long veterinarianId);

    // Find reports by pet owner
    @Query("SELECT mr FROM MedicalReport mr WHERE mr.pet.owner.id = :ownerId")
    List<MedicalReport> findByPetOwnerId(@Param("ownerId") Long ownerId);

    // Find reports by veterinarian for a specific pet
    List<MedicalReport> findByVeterinarianIdAndPetId(Long veterinarianId, Long petId);

    // Check if report code exists
    boolean existsByReportCode(String reportCode);

    // Find reports with pet and veterinarian details
    @Query("SELECT mr FROM MedicalReport mr JOIN FETCH mr.pet p JOIN FETCH mr.veterinarian v WHERE mr.id = :id")
    Optional<MedicalReport> findByIdWithDetails(@Param("id") Long id);

    // Find all reports with details
    @Query("SELECT mr FROM MedicalReport mr JOIN FETCH mr.pet p JOIN FETCH p.owner o ORDER BY mr.reportDate DESC")
    List<MedicalReport> findAllWithDetails();
}
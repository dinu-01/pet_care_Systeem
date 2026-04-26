package com.example.petcaresystem.repo;

import com.example.petcaresystem.model.GroomingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroomingRecordRepository extends JpaRepository<GroomingRecord, Long> {
    List<GroomingRecord> findByPetId(Long petId);
    List<GroomingRecord> findByPetIdAndIsActiveTrue(Long petId);
    List<GroomingRecord> findByOwnerId(Long ownerId);
}

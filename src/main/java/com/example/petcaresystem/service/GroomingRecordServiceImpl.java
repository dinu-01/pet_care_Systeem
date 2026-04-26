package com.example.petcaresystem.service;

import com.example.petcaresystem.model.GroomingRecord;
import com.example.petcaresystem.model.Pet;
import com.example.petcaresystem.model.User;
import com.example.petcaresystem.repo.GroomingRecordRepository;
import com.example.petcaresystem.repo.PetRepository;
import com.example.petcaresystem.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class GroomingRecordServiceImpl implements GroomingRecordService {

    private final GroomingRecordRepository groomingRecordRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    @Override
    public GroomingRecord addRecord(Long ownerId, Long petId, GroomingRecord record) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found: " + ownerId));
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found: " + petId));

        if (!pet.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("You can only add records to your own pet");
        }

        record.setOwner(owner);
        record.setPet(pet);
        record.setIsActive(true);

        log.info("Adding grooming record for pet: {}, owner: {}", pet.getName(), owner.getUsername());
        return groomingRecordRepository.save(record);
    }

    @Override
    public GroomingRecord updateRecord(Long ownerId, Long recordId, GroomingRecord changes) {
        GroomingRecord existing = groomingRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Grooming record not found: " + recordId));

        if (!existing.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("You can only update your own records");
        }

        if (changes.getServiceSummary() != null)
            existing.setServiceSummary(changes.getServiceSummary());
        if (changes.getNotes() != null)
            existing.setNotes(changes.getNotes());
        if (changes.getGroomerName() != null)
            existing.setGroomerName(changes.getGroomerName());
        if (changes.getPrice() != null)
            existing.setPrice(changes.getPrice());
        if (changes.getDurationMinutes() != null)
            existing.setDurationMinutes(changes.getDurationMinutes());
        if (changes.getPerformedAt() != null)
            existing.setPerformedAt(changes.getPerformedAt());
        if (changes.getNextSuggestedDate() != null)
            existing.setNextSuggestedDate(changes.getNextSuggestedDate());

        log.info("Updating grooming record: {}", recordId);
        return groomingRecordRepository.save(existing);
    }

    @Override
    public GroomingRecord softDeactivate(Long ownerId, Long recordId) {
        GroomingRecord record = groomingRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Grooming record not found: " + recordId));

        if (!record.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("You can only deactivate your own records");
        }

        record.setIsActive(false);
        log.info("Soft deactivating grooming record: {}", recordId);
        return groomingRecordRepository.save(record);
    }

    @Override
    public void deleteRecord(Long ownerId, Long recordId) {
        GroomingRecord record = groomingRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Grooming record not found: " + recordId));

        if (!record.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("You can only delete your own records");
        }

        log.info("Deleting grooming record: {}", recordId);
        groomingRecordRepository.delete(record);
    }

    @Override
    public GroomingRecord getById(Long ownerId, Long recordId) {
        GroomingRecord record = groomingRecordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Grooming record not found: " + recordId));

        if (!record.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("You can only view your own records");
        }

        return record;
    }

    @Override
    public List<GroomingRecord> getByPet(Long ownerId, Long petId, boolean activeOnly) {
        // Verify pet belongs to owner
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found: " + petId));

        if (!pet.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("You can only view records for your own pets");
        }

        List<GroomingRecord> records;
        if (activeOnly) {
            records = groomingRecordRepository.findByPetIdAndIsActiveTrue(petId);
        } else {
            records = groomingRecordRepository.findByPetId(petId);
        }

        // Sort by performed date (newest first)
        return records.stream()
                .sorted((r1, r2) -> r2.getPerformedAt().compareTo(r1.getPerformedAt()))
                .collect(Collectors.toList());
    }
    @Override
    public Object findById(Long recordId) {
        return groomingRecordRepository.findById(recordId);
    }


    @Override
    public List<GroomingRecord> getByOwner(Long ownerId) {
        // Verify owner exists
        userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found: " + ownerId));

        List<GroomingRecord> records = groomingRecordRepository.findByOwnerId(ownerId);

        // Sort by performed date (newest first)
        return records.stream()
                .sorted((r1, r2) -> r2.getPerformedAt().compareTo(r1.getPerformedAt()))
                .collect(Collectors.toList());
    }

}
package com.example.petcaresystem.service;

import com.example.petcaresystem.model.GroomingRecord;

import java.util.List;

public interface GroomingRecordService {
    GroomingRecord addRecord(Long ownerId, Long petId, GroomingRecord record);
    GroomingRecord updateRecord(Long ownerId, Long recordId, GroomingRecord changes);
    GroomingRecord softDeactivate(Long ownerId, Long recordId);
    void deleteRecord(Long ownerId, Long recordId);
    GroomingRecord getById(Long ownerId, Long recordId);
    List<GroomingRecord> getByPet(Long ownerId, Long petId, boolean activeOnly);
    List<GroomingRecord> getByOwner(Long ownerId);
    Object findById(Long recordId);

}

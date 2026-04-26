package com.example.petcaresystem.controller;

import com.example.petcaresystem.dto.GroomingRecordDTO;
import com.example.petcaresystem.model.GroomingRecord;
import com.example.petcaresystem.service.GroomingRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/grooming/records")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GroomingRecordController {

    private final GroomingRecordService groomingRecordService;

    private GroomingRecordDTO map(GroomingRecord r) {
        GroomingRecordDTO dto = new GroomingRecordDTO();
        dto.setId(r.getId());
        dto.setPetId(r.getPet() != null ? r.getPet().getId() : null);
        dto.setOwnerId(r.getOwner() != null ? r.getOwner().getId() : null);
        dto.setPetName(r.getPet() != null ? r.getPet().getName() : null);
        dto.setServiceSummary(r.getServiceSummary());
        dto.setNotes(r.getNotes());
        dto.setGroomerName(r.getGroomerName());
        dto.setPrice(r.getPrice());
        dto.setDurationMinutes(r.getDurationMinutes());
        dto.setPerformedAt(r.getPerformedAt());
        dto.setNextSuggestedDate(r.getNextSuggestedDate());
        dto.setIsActive(r.getIsActive());
        return dto;
    }

    @PostMapping("/owner/{ownerId}/pet/{petId}")
    public ResponseEntity<GroomingRecordDTO> addRecord(
            @PathVariable Long ownerId,
            @PathVariable Long petId,
            @RequestBody GroomingRecord payload) {
        return ResponseEntity.ok(map(groomingRecordService.addRecord(ownerId, petId, payload)));
    }

    @PutMapping("/owner/{ownerId}/{recordId}")
    public ResponseEntity<GroomingRecordDTO> update(
            @PathVariable Long ownerId,
            @PathVariable Long recordId,
            @RequestBody GroomingRecord payload) {
        return ResponseEntity.ok(map(groomingRecordService.updateRecord(ownerId, recordId, payload)));
    }

    @DeleteMapping("/owner/{ownerId}/{recordId}")
    public ResponseEntity<?> delete(
            @PathVariable Long ownerId,
            @PathVariable Long recordId) {
        groomingRecordService.deleteRecord(ownerId, recordId);
        return ResponseEntity.ok("🗑️ Grooming record deleted successfully");
    }

    @PatchMapping("/owner/{ownerId}/{recordId}/deactivate")
    public ResponseEntity<GroomingRecordDTO> deactivate(
            @PathVariable Long ownerId,
            @PathVariable Long recordId) {
        return ResponseEntity.ok(map(groomingRecordService.softDeactivate(ownerId, recordId)));
    }

    @GetMapping("/owner/{ownerId}/{recordId}")
    public ResponseEntity<GroomingRecordDTO> getOne(
            @PathVariable Long ownerId,
            @PathVariable Long recordId) {
        return ResponseEntity.ok(map(groomingRecordService.getById(ownerId, recordId)));
    }

    @GetMapping("/owner/{ownerId}/pet/{petId}")
    public ResponseEntity<List<GroomingRecordDTO>> getByPet(
            @PathVariable Long ownerId,
            @PathVariable Long petId) {

        List<GroomingRecordDTO> result = groomingRecordService
                .getByPet(ownerId, petId, false)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<GroomingRecordDTO>> getByOwner(
            @PathVariable Long ownerId) {

        List<GroomingRecordDTO> result = groomingRecordService
                .getByOwner(ownerId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

}

package com.example.petcaresystem.controller;

import com.example.petcaresystem.dto.AnnouncementDTO;
import com.example.petcaresystem.model.Announcement;
import com.example.petcaresystem.model.User;
import com.example.petcaresystem.repo.UserRepository;
import com.example.petcaresystem.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/announcements")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final UserRepository userRepository;

    // ✅ Helper to convert entity → DTO
    private AnnouncementDTO toDTO(Announcement a) {
        return AnnouncementDTO.builder()
                .id(a.getId())
                .title(a.getTitle())
                .message(a.getMessage())
                .createdBy(a.getCreatedBy())

                .targetRole(a.getTargetRole())

                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }

    // ✅ ADMIN ONLY — Create announcement
    @PostMapping("/create/{userId}")
    public ResponseEntity<?> create(@PathVariable Long userId, @RequestBody Announcement announcement) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403).body("🚫 Only ADMINs can create announcements");
        }

        if (announcement.getTargetRole() == null || announcement.getTargetRole().isBlank())
            announcement.setTargetRole("ALL");

        announcement.setCreatedBy(user.getUsername());
        return ResponseEntity.ok(toDTO(announcementService.createAnnouncement(announcement)));
    }

    // ✅ ADMIN ONLY — Update announcement
    @PutMapping("/update/{userId}/{id}")
    public ResponseEntity<?> update(@PathVariable Long userId, @PathVariable Long id, @RequestBody Announcement updated) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403).body("🚫 Only ADMINs can update announcements");
        }

        return ResponseEntity.ok(toDTO(announcementService.updateAnnouncement(id, updated)));
    }

    // ✅ ADMIN ONLY — Delete announcement
    @DeleteMapping("/delete/{userId}/{id}")
    public ResponseEntity<?> delete(@PathVariable Long userId, @PathVariable Long id) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(403).body("🚫 Only ADMINs can delete announcements");
        }

        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok("✅ Deleted successfully");
    }

    // ✅ ALL ROLES — View all
    @GetMapping("/view-all")
    public ResponseEntity<List<AnnouncementDTO>> getAll() {
        List<AnnouncementDTO> list = announcementService.getAllAnnouncements()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    // ✅ ALL ROLES — View by ID
    @GetMapping("/view/{id}")
    public ResponseEntity<AnnouncementDTO> getOne(@PathVariable Long id) {
        return announcementService.getAnnouncementById(id)
                .map(a -> ResponseEntity.ok(toDTO(a)))
                .orElse(ResponseEntity.notFound().build());
    }
}

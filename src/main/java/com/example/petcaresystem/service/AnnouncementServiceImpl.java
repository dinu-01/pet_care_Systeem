package com.example.petcaresystem.service;

import com.example.petcaresystem.model.Announcement;
import com.example.petcaresystem.repo.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;
    @Autowired private MailService mailService;

    @Override
    public Announcement createAnnouncement(Announcement announcement) {
        announcement.setCreatedAt(LocalDateTime.now());
        Announcement saved = announcementRepository.save(announcement);
        mailService.sendAnnouncementEmails(saved);        // NEW
        return saved;
    }

    @Override
    public Announcement updateAnnouncement(Long id, Announcement updated) {
        Announcement existing = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found with ID: " + id));
        existing.setTitle(updated.getTitle());
        existing.setMessage(updated.getMessage());
        existing.setUpdatedAt(LocalDateTime.now());
        return announcementRepository.save(existing);
    }

    @Override
    public void deleteAnnouncement(Long id) {
        if (!announcementRepository.existsById(id)) {
            throw new RuntimeException("Announcement not found");
        }
        announcementRepository.deleteById(id);
    }

    @Override
    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    @Override
    public Optional<Announcement> getAnnouncementById(Long id) {
        return announcementRepository.findById(id);
    }
}


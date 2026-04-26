package com.example.petcaresystem.service;

import com.example.petcaresystem.model.Announcement;
import java.util.List;
import java.util.Optional;

public interface AnnouncementService {
    Announcement createAnnouncement(Announcement announcement);
    Announcement updateAnnouncement(Long id, Announcement announcement);
    void deleteAnnouncement(Long id);
    List<Announcement> getAllAnnouncements();
    Optional<Announcement> getAnnouncementById(Long id);
}

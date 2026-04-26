package com.example.petcaresystem.service;

import com.example.petcaresystem.model.Announcement;

public interface MailService {
    void sendAnnouncementEmails(Announcement a);
}

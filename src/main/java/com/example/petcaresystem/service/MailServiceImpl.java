package com.example.petcaresystem.service;

import com.example.petcaresystem.model.Announcement;
import com.example.petcaresystem.model.User;
import com.example.petcaresystem.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Async
    @Override
    public void sendAnnouncementEmails(Announcement a) {
        // 1) Resolve recipients
        List<User> users = switch (a.getTargetRole().toUpperCase()) {
            case "ALL" -> userRepository.findAll();
            case "PET_OWNER" -> {
                // if you keep special-case owners
                List<User> owners = userRepository.findAllPetOwners();
                yield owners.isEmpty() ? userRepository.findByRole("PET_OWNER") : owners;
            }
            case "VETERINARIAN" -> userRepository.findByRole("VETERINARIAN");
            case "SHOP_KEEPER" -> userRepository.findByRole("SHOP_KEEPER");
            default -> new ArrayList<>();
        };

        if (users.isEmpty()) return;

        // 2) Build a single BCC email (cheap + efficient)
        List<String> emails = users.stream()
                .map(User::getEmail)
                .filter(e -> e != null && !e.isBlank())
                .distinct()
                .toList();


        if (emails.isEmpty()) return;

        // 3) Send (chunk BCC to avoid provider limits, e.g., 50 per email)
        int chunk = 50;
        for (int i = 0; i < emails.size(); i += chunk) {
            List<String> batch = emails.subList(i, Math.min(i + chunk, emails.size()));

            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setSubject("📢 " + a.getTitle());
            msg.setText("""
                    Hello,

                    %s

                    — %s (PetCare System)
                    """.formatted(a.getMessage(), a.getCreatedBy()));
            // From address is taken from spring.mail.username unless overridden:
            // msg.setFrom("PetCare <no-reply@yourdomain>");
            msg.setBcc(batch.toArray(String[]::new));

            try { mailSender.send(msg); } catch (Exception ignored) { /* log if needed */ }
        }
    }
}

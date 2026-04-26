package com.example.petcaresystem.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnouncementDTO {
    private Long id;
    private String title;
    private String message;
    private String createdBy;
    private String targetRole; //
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

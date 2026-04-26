package com.example.petcaresystem.dto;

import com.example.petcaresystem.model.Feedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {

    private Long id;
    private Feedback.FeedbackType feedbackType;
    private Integer rating;
    private Feedback.FeedbackCategory category;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long ownerId;
    private String ownerName;


    public static Object fromEntity(Feedback feedback) {

        return null;
    }
}
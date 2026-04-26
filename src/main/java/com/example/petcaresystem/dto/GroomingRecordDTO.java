package com.example.petcaresystem.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class GroomingRecordDTO {
    private Long id;
    private Long petId;
    private Long ownerId;
    private String petName;

    private String serviceSummary;
    private String notes;
    private String groomerName;
    private Double price;
    private Integer durationMinutes;
    private LocalDateTime performedAt;
    private LocalDateTime nextSuggestedDate;
    private Boolean isActive;
}

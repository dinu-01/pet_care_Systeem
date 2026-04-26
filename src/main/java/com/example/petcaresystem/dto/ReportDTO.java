package com.example.petcaresystem.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDTO {
    private long totalPets;
    private long totalAppointments;
    private long totalMedicalReports;
    private long totalAnnouncements;
    private long completedAppointments;
    private long pendingAppointments;
    private long activeAnnouncements;
}
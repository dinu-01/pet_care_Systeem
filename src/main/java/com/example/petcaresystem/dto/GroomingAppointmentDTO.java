package com.example.petcaresystem.dto;

import com.example.petcaresystem.model.Appointment;
import java.time.LocalDateTime;

public class GroomingAppointmentDTO {
    private Long id;
    private LocalDateTime dateTime;
    private String petName;
    private Long petId;
    private Long ownerId;
    private Appointment.AppointmentStatus status;
    private String description;

    public GroomingAppointmentDTO() {}

    public GroomingAppointmentDTO(Appointment a) {
        this.id = a.getId();
        this.dateTime = a.getAppointmentDateTime();
        this.petName = a.getPet().getName();
        this.petId = a.getPet().getId();
        this.ownerId = a.getOwner().getId();
        this.status = a.getStatus();
        this.description = a.getDescription();
    }

    // getters/setters ...
}

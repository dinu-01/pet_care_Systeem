package com.example.petcaresystem.dto;

import com.example.petcaresystem.model.Appointment;
import jakarta.validation.constraints.NotNull;

public class CreateAppointmentRequest {

    @NotNull(message = "Pet ID is required")
    private Long petId;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    private Long serviceProviderId;

    @NotNull(message = "Appointment details are required")
    private Appointment appointment;

    // Constructors
    public CreateAppointmentRequest() {}

    public CreateAppointmentRequest(Long petId, Long ownerId, Long serviceProviderId, Appointment appointment) {
        this.petId = petId;
        this.ownerId = ownerId;
        this.serviceProviderId = serviceProviderId;
        this.appointment = appointment;
    }

    // Getters and Setters
    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public Long getServiceProviderId() { return serviceProviderId; }
    public void setServiceProviderId(Long serviceProviderId) { this.serviceProviderId = serviceProviderId; }

    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
}
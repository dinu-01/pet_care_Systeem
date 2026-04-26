package com.example.petcaresystem.dto;

import com.example.petcaresystem.model.Appointment;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class AppointmentDTO {
    private Long id;

    @NotNull(message = "Service type is required")
    private Appointment.AppointmentType serviceType;

    @Future(message = "Appointment date must be in the future")
    @NotNull(message = "Appointment date time is required")
    private LocalDateTime appointmentDateTime;

    private String description;

    private Appointment.AppointmentStatus status;
    private Long petId;
    private Long ownerId;
    private Long serviceProviderId;
    private String petName;
    private String ownerName;
    private String serviceProviderName;
    private String petSpecies;
    private String petBreed;
    private String cancellationReason;

    // Constructors
    public AppointmentDTO() {}

    public AppointmentDTO(Appointment appointment) {
        this.id = appointment.getId();
        this.serviceType = appointment.getServiceType();
        this.appointmentDateTime = appointment.getAppointmentDateTime();
        this.description = appointment.getDescription();
        this.status = appointment.getStatus();
        this.petId = appointment.getPet().getId();
        this.ownerId = appointment.getOwner().getId();
        this.petName = appointment.getPet().getName();
        this.ownerName = appointment.getOwner().getUsername();
        this.petSpecies = appointment.getPet().getSpecies();
        this.petBreed = appointment.getPet().getBreed();
        this.cancellationReason = appointment.getCancellationReason();

        if (appointment.getServiceProvider() != null) {
            this.serviceProviderId = appointment.getServiceProvider().getId();
            this.serviceProviderName = appointment.getServiceProvider().getName();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Appointment.AppointmentType getServiceType() { return serviceType; }
    public void setServiceType(Appointment.AppointmentType serviceType) { this.serviceType = serviceType; }

    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Appointment.AppointmentStatus getStatus() { return status; }
    public void setStatus(Appointment.AppointmentStatus status) { this.status = status; }

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public Long getServiceProviderId() { return serviceProviderId; }
    public void setServiceProviderId(Long serviceProviderId) { this.serviceProviderId = serviceProviderId; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getServiceProviderName() { return serviceProviderName; }
    public void setServiceProviderName(String serviceProviderName) { this.serviceProviderName = serviceProviderName; }

    public String getPetSpecies() { return petSpecies; }
    public void setPetSpecies(String petSpecies) { this.petSpecies = petSpecies; }

    public String getPetBreed() { return petBreed; }
    public void setPetBreed(String petBreed) { this.petBreed = petBreed; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
}
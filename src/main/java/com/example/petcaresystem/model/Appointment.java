package com.example.petcaresystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Service type is required")
    private AppointmentType serviceType;

    @Column(name = "appointment_date_time")
    @Future(message = "Appointment date must be in the future")
    @NotNull(message = "Appointment date time is required")
    private LocalDateTime appointmentDateTime;

    private String description;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    @NotNull(message = "Pet is required")
    private Pet pet;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    @NotNull(message = "Owner is required")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "service_provider_id")
    private ServiceProvider serviceProvider;

    private String cancellationReason;

    // Enums
    public enum AppointmentType {
        GROOMING, VETERINARY_CARE, VACCINATION
    }

    public enum AppointmentStatus {
        SCHEDULED, CONFIRMED, COMPLETED, RESCHEDULED, pending, PENDING, CANCELLED
    }

    // Constructors
    public Appointment() {}

    public Appointment(AppointmentType serviceType, LocalDateTime appointmentDateTime,
                       String description, Pet pet, User owner, ServiceProvider serviceProvider) {
        this.serviceType = serviceType;
        this.appointmentDateTime = appointmentDateTime;
        this.description = description;
        this.pet = pet;
        this.owner = owner;
        this.serviceProvider = serviceProvider;
        this.status = AppointmentStatus.SCHEDULED;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AppointmentType getServiceType() { return serviceType; }
    public void setServiceType(AppointmentType serviceType) { this.serviceType = serviceType; }

    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public Pet getPet() { return pet; }
    public void setPet(Pet pet) { this.pet = pet; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public ServiceProvider getServiceProvider() { return serviceProvider; }
    public void setServiceProvider(ServiceProvider serviceProvider) { this.serviceProvider = serviceProvider; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    // Helper methods
    public boolean isUpcoming() {
        return appointmentDateTime.isAfter(LocalDateTime.now()) &&
                (status == AppointmentStatus.SCHEDULED || status == AppointmentStatus.CONFIRMED);
    }

    public boolean canBeCancelled() {
        return appointmentDateTime.isAfter(LocalDateTime.now()) &&
                status != AppointmentStatus.CANCELLED && status != AppointmentStatus.COMPLETED;
    }

    public boolean canBeRescheduled() {
        return appointmentDateTime.isAfter(LocalDateTime.now()) &&
                status != AppointmentStatus.CANCELLED && status != AppointmentStatus.COMPLETED;
    }
}
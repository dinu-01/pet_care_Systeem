package com.example.petcaresystem.service;

import com.example.petcaresystem.dto.AvailableSlotDTO;
import com.example.petcaresystem.model.Appointment;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AppointmentService {

    // CRUD Operations
    Appointment createAppointment(Appointment appointment, Long petId, Long ownerId, Long serviceProviderId);
    List<Appointment> getAllAppointments();
    Appointment getAppointmentById(Long id);
    Appointment updateAppointment(Long id, Appointment appointmentDetails);
    void deleteAppointment(Long id);
    Appointment confirmPendingAppointment(Long appointmentId);

    // Query Operations
    List<Appointment> getAppointmentsByOwner(Long ownerId);
    List<Appointment> getAppointmentsByPet(Long petId);
    List<Appointment> getAppointmentsByServiceType(Appointment.AppointmentType serviceType);
    List<Appointment> getAppointmentsByStatus(Appointment.AppointmentStatus status);
    List<Appointment> getUpcomingAppointments();
    List<Appointment> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end);
    List<Appointment> getAppointmentsByServiceProvider(Long serviceProviderId);

    // Business Logic
    Appointment updateAppointmentStatus(Long id, Appointment.AppointmentStatus status);
    boolean hasTimeConflict(LocalDateTime dateTime, Long excludedAppointmentId);
    boolean validateAppointmentOwnership(Long appointmentId, Long ownerId);

    // Manager Operations
    List<Appointment> getAppointmentsForManager();
    Appointment rescheduleAppointment(Long appointmentId, LocalDateTime newDateTime);
    Appointment cancelAppointment(Long appointmentId, String reason);
    Appointment confirmAppointmentCompletion(Long appointmentId);
    List<Appointment> getConflictingAppointments(LocalDateTime dateTime);
    Map<String, Long> getAppointmentStatistics();
    List<Appointment> getTodaysAppointments();

    // Availability Operations
    List<AvailableSlotDTO> getAvailableTimeSlots(LocalDate date, Appointment.AppointmentType serviceType);
    List<AvailableSlotDTO> getAvailableSlotsForProvider(Long providerId, LocalDate date);

    // Get all available slots without service type filter
    List<AvailableSlotDTO> getAllAvailableTimeSlots(LocalDate date);

    boolean isTimeSlotAvailable(LocalDateTime dateTime, Long serviceProviderId);

    // : Check availability without specific provider
    boolean isTimeSlotAvailable(LocalDateTime dateTime);
}
package com.example.petcaresystem.repo;

import com.example.petcaresystem.model.Appointment;
import com.example.petcaresystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Find appointments by owner
    List<Appointment> findByOwner(User owner);
    List<Appointment> findByOwnerId(Long ownerId);

    // Find appointments by pet
    List<Appointment> findByPetId(Long petId);

    // Find appointments by service type
    List<Appointment> findByServiceType(Appointment.AppointmentType serviceType);

    // Find appointments by status
    List<Appointment> findByStatus(Appointment.AppointmentStatus status);

    // Find appointments by owner and status
    List<Appointment> findByOwnerAndStatus(User owner, Appointment.AppointmentStatus status);

    // Find appointments by service provider
    List<Appointment> findByServiceProviderId(Long serviceProviderId);

    // Find upcoming appointments (future dates with specific statuses)
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDateTime > :dateTime AND a.status IN :statuses")
    List<Appointment> findUpcomingAppointments(
            @Param("dateTime") LocalDateTime dateTime,
            @Param("statuses") List<Appointment.AppointmentStatus> statuses
    );

    // Find appointments by date range
    List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);

    // Check for time conflicts
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDateTime = :dateTime AND a.status IN :statuses")
    List<Appointment> findConflictingAppointments(
            @Param("dateTime") LocalDateTime dateTime,
            @Param("statuses") List<Appointment.AppointmentStatus> statuses
    );

    // Find appointments by service provider and date range
    @Query("SELECT a FROM Appointment a WHERE a.serviceProvider.id = :providerId AND a.appointmentDateTime BETWEEN :start AND :end")
    List<Appointment> findByServiceProviderAndDateRange(
            @Param("providerId") Long providerId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // Count appointments by status
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = :status")
    Long countByStatus(@Param("status") Appointment.AppointmentStatus status);
}
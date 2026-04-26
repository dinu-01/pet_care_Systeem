package com.example.petcaresystem.controller;

import com.example.petcaresystem.dto.AppointmentDTO;
import com.example.petcaresystem.dto.AvailableSlotDTO;
import com.example.petcaresystem.dto.CreateAppointmentRequest;
import com.example.petcaresystem.model.Appointment;
import com.example.petcaresystem.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping(value = "api/v1/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // NEW: Create appointment with request body (Better for frontend)
    @PostMapping("/create")
    public ResponseEntity<?> createAppointment(@Valid @RequestBody CreateAppointmentRequest request) {
        try {
            Appointment createdAppointment = appointmentService.createAppointment(
                    request.getAppointment(),
                    request.getPetId(),
                    request.getOwnerId(),
                    request.getServiceProviderId()
            );
            AppointmentDTO appointmentDTO = new AppointmentDTO(createdAppointment);
            return ResponseEntity.ok(appointmentDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //  Keep existing endpoint for backward compatibility
    @PostMapping("/create/{petId}/{ownerId}")
    public ResponseEntity<?> createAppointmentWithPath(
            @PathVariable Long petId,
            @PathVariable Long ownerId,
            @RequestParam(required = false) Long serviceProviderId,
            @Valid @RequestBody Appointment appointment) {
        try {
            Appointment createdAppointment = appointmentService.createAppointment(appointment, petId, ownerId, serviceProviderId);
            AppointmentDTO appointmentDTO = new AppointmentDTO(createdAppointment);
            return ResponseEntity.ok(appointmentDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get all appointments
    @GetMapping("/all")
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(appointmentDTOs);
    }

    // Get appointment by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            AppointmentDTO appointmentDTO = new AppointmentDTO(appointment);
            return ResponseEntity.ok(appointmentDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get appointments by owner
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByOwner(@PathVariable Long ownerId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByOwner(ownerId);
        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(appointmentDTOs);
    }

    // Get appointments by pet
    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByPet(@PathVariable Long petId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByPet(petId);
        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(appointmentDTOs);
    }

    // Get appointments by service type
    @GetMapping("/type/{serviceType}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByServiceType(
            @PathVariable Appointment.AppointmentType serviceType) {
        List<Appointment> appointments = appointmentService.getAppointmentsByServiceType(serviceType);
        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(appointmentDTOs);
    }

    // Get appointments by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByStatus(
            @PathVariable Appointment.AppointmentStatus status) {
        List<Appointment> appointments = appointmentService.getAppointmentsByStatus(status);
        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(appointmentDTOs);
    }

    // Get upcoming appointments
    @GetMapping("/upcoming")
    public ResponseEntity<List<AppointmentDTO>> getUpcomingAppointments() {
        List<Appointment> appointments = appointmentService.getUpcomingAppointments();
        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(appointmentDTOs);
    }

    // Get appointments by date range
    @GetMapping("/range")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDateRange(start, end);
        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(appointmentDTOs);
    }

    // NEW: Get available time slots without service type
    @GetMapping("/available-slots")
    public ResponseEntity<List<AvailableSlotDTO>> getAvailableTimeSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Appointment.AppointmentType serviceType) {
        List<AvailableSlotDTO> availableSlots;

        if (serviceType != null) {
            availableSlots = appointmentService.getAvailableTimeSlots(date, serviceType);
        } else {
            // Get all available slots for all service types
            availableSlots = appointmentService.getAllAvailableTimeSlots(date);
        }

        return ResponseEntity.ok(availableSlots);
    }

    // Get available slots for specific provider
    @GetMapping("/provider/{providerId}/available-slots")
    public ResponseEntity<List<AvailableSlotDTO>> getAvailableSlotsForProvider(
            @PathVariable Long providerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AvailableSlotDTO> availableSlots = appointmentService.getAvailableSlotsForProvider(providerId, date);
        return ResponseEntity.ok(availableSlots);
    }

    // Check time slot availability
    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkTimeSlotAvailability(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
            @RequestParam(required = false) Long serviceProviderId) {
        boolean isAvailable;

        if (serviceProviderId != null) {
            isAvailable = appointmentService.isTimeSlotAvailable(dateTime, serviceProviderId);
        } else {
            isAvailable = appointmentService.isTimeSlotAvailable(dateTime);
        }

        return ResponseEntity.ok(isAvailable);
    }

    // Update appointment
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody Appointment appointmentDetails) {
        try {
            Appointment updatedAppointment = appointmentService.updateAppointment(id, appointmentDetails);
            AppointmentDTO appointmentDTO = new AppointmentDTO(updatedAppointment);
            return ResponseEntity.ok(appointmentDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update appointment status
    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable Long id,
            @PathVariable Appointment.AppointmentStatus status) {
        try {
            Appointment updatedAppointment = appointmentService.updateAppointmentStatus(id, status);
            AppointmentDTO appointmentDTO = new AppointmentDTO(updatedAppointment);
            return ResponseEntity.ok(appointmentDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete appointment
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.ok().body("Appointment deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Check time conflict
    @GetMapping("/check-conflict")
    public ResponseEntity<Boolean> checkTimeConflict(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
            @RequestParam(required = false) Long excludeAppointmentId) {
        boolean hasConflict = appointmentService.hasTimeConflict(dateTime, excludeAppointmentId);
        return ResponseEntity.ok(hasConflict);
    }

    // Validate appointment ownership
    @GetMapping("/{appointmentId}/validate-owner/{ownerId}")
    public ResponseEntity<Boolean> validateAppointmentOwnership(
            @PathVariable Long appointmentId,
            @PathVariable Long ownerId) {
        boolean isValid = appointmentService.validateAppointmentOwnership(appointmentId, ownerId);
        return ResponseEntity.ok(isValid);
    }

    // Manager endpoints
    @GetMapping("/manager/all")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsForManager() {
        List<Appointment> appointments = appointmentService.getAppointmentsForManager();
        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(appointmentDTOs);
    }

    @PutMapping("/manager/{id}/reschedule")
    public ResponseEntity<?> rescheduleAppointment(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDateTime) {
        try {
            Appointment appointment = appointmentService.rescheduleAppointment(id, newDateTime);
            AppointmentDTO appointmentDTO = new AppointmentDTO(appointment);
            return ResponseEntity.ok(appointmentDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/manager/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        try {
            Appointment appointment = appointmentService.cancelAppointment(id, reason);
            AppointmentDTO appointmentDTO = new AppointmentDTO(appointment);
            return ResponseEntity.ok(appointmentDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/manager/{id}/complete")
    public ResponseEntity<?> confirmAppointmentCompletion(@PathVariable Long id) {
        try {
            Appointment appointment = appointmentService.confirmAppointmentCompletion(id);
            AppointmentDTO appointmentDTO = new AppointmentDTO(appointment);
            return ResponseEntity.ok(appointmentDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/manager/conflicts")
    public ResponseEntity<List<AppointmentDTO>> getConflictingAppointments(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime) {
        List<Appointment> conflicts = appointmentService.getConflictingAppointments(dateTime);
        List<AppointmentDTO> conflictDTOs = conflicts.stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(conflictDTOs);
    }

    @GetMapping("/manager/statistics")
    public ResponseEntity<Map<String, Long>> getAppointmentStatistics() {
        Map<String, Long> stats = appointmentService.getAppointmentStatistics();
        return ResponseEntity.ok(stats);
    }
    // Get appointments by Service Provider (Groomer)
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByProvider(@PathVariable Long providerId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByServiceProvider(providerId);
        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(appointmentDTOs);
    }


    @GetMapping("/manager/today")
    public ResponseEntity<List<AppointmentDTO>> getTodaysAppointments() {
        List<Appointment> appointments = appointmentService.getTodaysAppointments();
        List<AppointmentDTO> appointmentDTOs = appointments.stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(appointmentDTOs);
    }
}
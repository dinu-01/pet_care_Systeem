package com.example.petcaresystem.controller;

import com.example.petcaresystem.model.Appointment;
import com.example.petcaresystem.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/appointment-manager")
@CrossOrigin(origins = "*")
public class AppointmentManagerController {

    @Autowired
    private AppointmentService appointmentService;

    // 1️⃣ View all appointments
    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAppointmentsForManager());
    }

    // 2️⃣ View upcoming appointments
    @GetMapping("/appointments/upcoming")
    public ResponseEntity<List<Appointment>> getUpcomingAppointments() {
        return ResponseEntity.ok(appointmentService.getUpcomingAppointments());
    }

    // 3️⃣ View today's appointments
    @GetMapping("/appointments/today")
    public ResponseEntity<List<Appointment>> getTodayAppointments() {
        return ResponseEntity.ok(appointmentService.getTodaysAppointments());
    }

    // 4️⃣ Confirm appointment
    @PutMapping("/appointments/confirm/{id}")
    public ResponseEntity<?> confirmAppointment(@PathVariable Long id) {
        try {
            Appointment confirmed = appointmentService.updateAppointmentStatus(id, Appointment.AppointmentStatus.CONFIRMED);
            return ResponseEntity.ok(confirmed);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 5️⃣ Mark appointment as completed
    @PutMapping("/appointments/complete/{id}")
    public ResponseEntity<?> completeAppointment(@PathVariable Long id) {
        try {
            Appointment completed = appointmentService.confirmAppointmentCompletion(id);
            return ResponseEntity.ok(completed);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 6️⃣ Cancel appointment
    @PutMapping("/appointments/cancel/{id}")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id, @RequestParam String reason) {
        try {
            Appointment cancelled = appointmentService.cancelAppointment(id, reason);
            return ResponseEntity.ok(cancelled);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 7️⃣ Reschedule appointment
    @PutMapping("/appointments/reschedule/{id}")
    public ResponseEntity<?> rescheduleAppointment(@PathVariable Long id, @RequestParam String newDateTime) {
        try {
            LocalDateTime newTime = LocalDateTime.parse(newDateTime);
            Appointment rescheduled = appointmentService.rescheduleAppointment(id, newTime);
            return ResponseEntity.ok(rescheduled);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 8️⃣ Delete appointment
    @DeleteMapping("/appointments/delete/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.ok("Appointment deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 9️⃣ Get appointment statistics
    @GetMapping("/appointments/stats")
    public ResponseEntity<Map<String, Long>> getAppointmentStats() {
        return ResponseEntity.ok(appointmentService.getAppointmentStatistics());
    }
}

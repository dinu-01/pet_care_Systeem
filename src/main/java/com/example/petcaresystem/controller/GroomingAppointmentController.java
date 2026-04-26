package com.example.petcaresystem.controller;

import com.example.petcaresystem.dto.GroomingAppointmentDTO;
import com.example.petcaresystem.model.Appointment;
import com.example.petcaresystem.service.GroomingAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/grooming/appointments")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GroomingAppointmentController {

    private final GroomingAppointmentService groomingAppointmentService;

    // Owner can see ONLY their grooming appointments
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<GroomingAppointmentDTO>> getOwnerGroomingAppointments(@PathVariable Long ownerId) {
        List<Appointment> list = groomingAppointmentService.getGroomingAppointmentsForOwner(ownerId);
        return ResponseEntity.ok(list.stream().map(GroomingAppointmentDTO::new).collect(Collectors.toList()));
    }

    // Owner marks a grooming appointment as COMPLETED (only their own)
    @PutMapping("/{appointmentId}/complete/owner/{ownerId}")
    public ResponseEntity<GroomingAppointmentDTO> markComplete(
            @PathVariable Long appointmentId, @PathVariable Long ownerId) {
        Appointment updated = groomingAppointmentService.markGroomingAppointmentCompleted(appointmentId, ownerId);
        return ResponseEntity.ok(new GroomingAppointmentDTO(updated));
    }
}

package com.example.petcaresystem.service;

import com.example.petcaresystem.model.Appointment;
import java.util.List;

public interface GroomingAppointmentService {
    List<Appointment> getGroomingAppointmentsForOwner(Long ownerId);
    Appointment markGroomingAppointmentCompleted(Long appointmentId, Long ownerId);
}

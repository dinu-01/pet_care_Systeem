package com.example.petcaresystem.service;

import com.example.petcaresystem.model.Appointment;
import com.example.petcaresystem.repo.AppointmentRepository;
import com.example.petcaresystem.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class GroomingAppointmentServiceImpl implements GroomingAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Appointment> getGroomingAppointmentsForOwner(Long ownerId) {
        if (!userRepository.existsById(ownerId)) {
            throw new RuntimeException("Owner not found with id: " + ownerId);
        }
        // filter owner's appointments by type=GROOMING
        return appointmentRepository.findByOwnerId(ownerId).stream()
                .filter(a -> a.getServiceType() == Appointment.AppointmentType.GROOMING)
                .collect(Collectors.toList());
    }

    @Override
    public Appointment markGroomingAppointmentCompleted(Long appointmentId, Long ownerId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found: " + appointmentId));

        if (!appt.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("You cannot update another owner's appointment");
        }
        if (appt.getServiceType() != Appointment.AppointmentType.GROOMING) {
            throw new RuntimeException("Not a grooming appointment");
        }
        if (appt.getAppointmentDateTime().isAfter(java.time.LocalDateTime.now())) {
            throw new RuntimeException("Cannot mark a future appointment as completed");
        }

        appt.setStatus(Appointment.AppointmentStatus.COMPLETED);
        return appointmentRepository.save(appt);
    }
}

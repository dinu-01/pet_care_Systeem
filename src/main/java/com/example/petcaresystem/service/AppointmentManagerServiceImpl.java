package com.example.petcaresystem.service;

import com.example.petcaresystem.model.AppointmentManager;
import com.example.petcaresystem.model.User;
import com.example.petcaresystem.repo.AppointmentManagerRepository;
import com.example.petcaresystem.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppointmentManagerServiceImpl implements AppointmentManagerService {

    @Autowired
    private AppointmentManagerRepository appointmentManagerRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public AppointmentManager createAppointmentManager(AppointmentManager manager, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (!user.getRole().equals("APPOINTMENT_MANAGER")) {
            throw new RuntimeException("User must have APPOINTMENT_MANAGER role");
        }

        if (appointmentManagerRepository.existsByUserId(userId)) {
            throw new RuntimeException("Appointment manager already exists for this user");
        }

        manager.setUser(user);
        return appointmentManagerRepository.save(manager);
    }

    @Override
    public List<AppointmentManager> getAllAppointmentManagers() {
        return appointmentManagerRepository.findAll();
    }

    @Override
    public Optional<AppointmentManager> getAppointmentManagerById(Long id) {
        return appointmentManagerRepository.findById(id);
    }

    @Override
    public Optional<AppointmentManager> getAppointmentManagerByUserId(Long userId) {
        return appointmentManagerRepository.findByUserId(userId);
    }

    @Override
    public AppointmentManager updateAppointmentManager(Long id, AppointmentManager managerDetails) {
        AppointmentManager existingManager = appointmentManagerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment manager not found with id: " + id));

        if (managerDetails.getName() != null) {
            existingManager.setName(managerDetails.getName());
        }

        if (managerDetails.getEmail() != null) {
            existingManager.setEmail(managerDetails.getEmail());
        }

        return appointmentManagerRepository.save(existingManager);
    }

    @Override
    public void deleteAppointmentManager(Long id) {
        AppointmentManager manager = appointmentManagerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment manager not found with id: " + id));
        appointmentManagerRepository.delete(manager);
    }

    @Override
    public boolean isUserAppointmentManager(Long userId) {
        return appointmentManagerRepository.existsByUserId(userId);
    }
}
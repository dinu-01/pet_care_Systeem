package com.example.petcaresystem.service;

import com.example.petcaresystem.model.AppointmentManager;
import java.util.List;
import java.util.Optional;

public interface AppointmentManagerService {
    AppointmentManager createAppointmentManager(AppointmentManager manager, Long userId);
    List<AppointmentManager> getAllAppointmentManagers();
    Optional<AppointmentManager> getAppointmentManagerById(Long id);
    Optional<AppointmentManager> getAppointmentManagerByUserId(Long userId);
    AppointmentManager updateAppointmentManager(Long id, AppointmentManager managerDetails);
    void deleteAppointmentManager(Long id);
    boolean isUserAppointmentManager(Long userId);
}
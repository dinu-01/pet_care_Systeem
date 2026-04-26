package com.example.petcaresystem.repo;

import com.example.petcaresystem.model.AppointmentManager;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AppointmentManagerRepository extends JpaRepository<AppointmentManager, Long> {
    Optional<AppointmentManager> findByUserId(Long userId);
    Optional<AppointmentManager> findByEmail(String email);
    boolean existsByUserId(Long userId);
}
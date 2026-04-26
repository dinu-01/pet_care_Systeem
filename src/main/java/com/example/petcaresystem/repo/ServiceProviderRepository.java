package com.example.petcaresystem.repo;

import com.example.petcaresystem.model.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
    Optional<ServiceProvider> findByUserId(Long userId);
    Optional<ServiceProvider> findByEmail(String email);
    List<ServiceProvider> findBySpecialization(String specialization);
    boolean existsByUserId(Long userId);
    List<ServiceProvider> findByAvailableDaysContaining(String dayOfWeek);
}
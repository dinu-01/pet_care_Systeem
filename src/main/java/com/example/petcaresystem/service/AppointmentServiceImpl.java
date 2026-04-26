package com.example.petcaresystem.service;

import com.example.petcaresystem.dto.AvailableSlotDTO;
import com.example.petcaresystem.model.Appointment;
import com.example.petcaresystem.model.Pet;
import com.example.petcaresystem.model.ServiceProvider;
import com.example.petcaresystem.model.User;
import com.example.petcaresystem.repo.AppointmentRepository;
import com.example.petcaresystem.repo.PetRepository;
import com.example.petcaresystem.repo.ServiceProviderRepository;
import com.example.petcaresystem.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Override
    public Appointment createAppointment(Appointment appointment, Long petId, Long ownerId, Long serviceProviderId) {
        // Validate pet exists
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found with id: " + petId));

        // Validate owner exists
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + ownerId));

        // Validate service provider exists
        ServiceProvider serviceProvider = null;
        if (serviceProviderId != null) {
            serviceProvider = serviceProviderRepository.findById(serviceProviderId)
                    .orElseThrow(() -> new RuntimeException("Service provider not found with id: " + serviceProviderId));
        }

        // Verify pet belongs to owner
        if (!pet.getOwner().getId().equals(ownerId)) {
            throw new RuntimeException("Pet does not belong to the specified owner");
        }

        // Validate appointment date is in the future
        if (appointment.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Appointment date must be in the future");
        }

        // Check for time conflicts
        if (hasTimeConflict(appointment.getAppointmentDateTime(), null)) {
            throw new RuntimeException("Time slot is already booked. Please choose a different time.");
        }

        // Check if service provider is available at this time
        if (serviceProvider != null && !isTimeSlotAvailable(appointment.getAppointmentDateTime(), serviceProviderId)) {
            throw new RuntimeException("Service provider is not available at this time");
        }

        // Set relationships
        appointment.setPet(pet);
        appointment.setOwner(owner);
        appointment.setServiceProvider(serviceProvider);
        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);

        return appointmentRepository.save(appointment);
    }

    // : Get all available slots without service type filter
    @Override
    public List<AvailableSlotDTO> getAllAvailableTimeSlots(LocalDate date) {
        List<AvailableSlotDTO> allSlots = new ArrayList<>();
        List<ServiceProvider> allProviders = serviceProviderRepository.findAll();

        for (ServiceProvider provider : allProviders) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (provider.isAvailableOn(dayOfWeek)) {
                allSlots.addAll(generateSlotsForProvider(provider, date));
            }
        }

        // Sort by time
        allSlots.sort(Comparator.comparing(AvailableSlotDTO::getStartTime));
        return allSlots;
    }

    // : Check availability without specific provider
    @Override
    public boolean isTimeSlotAvailable(LocalDateTime dateTime) {
        List<ServiceProvider> providers = serviceProviderRepository.findAll();

        for (ServiceProvider provider : providers) {
            DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
            LocalTime time = dateTime.toLocalTime();

            // Check if provider works on this day and time
            if (provider.isAvailableOn(dayOfWeek) && provider.isWithinWorkingHours(time)) {
                // Check for existing appointments at this time
                List<Appointment.AppointmentStatus> conflictingStatuses = Arrays.asList(
                        Appointment.AppointmentStatus.SCHEDULED,
                        Appointment.AppointmentStatus.CONFIRMED
                );

                List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(dateTime, conflictingStatuses);

                // Filter conflicts for this specific provider
                List<Appointment> providerConflicts = conflicts.stream()
                        .filter(appointment -> appointment.getServiceProvider() != null &&
                                appointment.getServiceProvider().getId().equals(provider.getId()))
                        .toList();

                if (providerConflicts.isEmpty()) {
                    return true; // At least one provider is available
                }
            }
        }

        return false; // No providers available at this time
    }

    // Rest of the existing methods remain the same...
    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
    }

    @Override
    public Appointment updateAppointment(Long id, Appointment appointmentDetails) {
        Appointment existingAppointment = getAppointmentById(id);

        // Check for time conflicts (excluding current appointment)
        if (hasTimeConflict(appointmentDetails.getAppointmentDateTime(), id)) {
            throw new RuntimeException("Time slot is already booked. Please choose a different time.");
        }

        // Validate appointment date is in the future
        if (appointmentDetails.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Appointment date must be in the future");
        }

        // Update fields
        existingAppointment.setServiceType(appointmentDetails.getServiceType());
        existingAppointment.setAppointmentDateTime(appointmentDetails.getAppointmentDateTime());
        existingAppointment.setDescription(appointmentDetails.getDescription());

        if (appointmentDetails.getServiceProvider() != null) {
            existingAppointment.setServiceProvider(appointmentDetails.getServiceProvider());
        }

        return appointmentRepository.save(existingAppointment);
    }

    @Override
    public void deleteAppointment(Long id) {
        Appointment appointment = getAppointmentById(id);

        // Only allow deletion if appointment is in the future
        if (appointment.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot delete past appointments");
        }

        appointmentRepository.deleteById(id);
    }

    @Override
    public List<Appointment> getAppointmentsByOwner(Long ownerId) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + ownerId));
        return appointmentRepository.findByOwner(owner);
    }

    @Override
    public List<Appointment> getAppointmentsByPet(Long petId) {
        return appointmentRepository.findByPetId(petId);
    }

    @Override
    public List<Appointment> getAppointmentsByServiceType(Appointment.AppointmentType serviceType) {
        return appointmentRepository.findByServiceType(serviceType);
    }

    @Override
    public List<Appointment> getAppointmentsByStatus(Appointment.AppointmentStatus status) {
        return appointmentRepository.findByStatus(status);
    }

    @Override
    public List<Appointment> getAppointmentsByServiceProvider(Long serviceProviderId) {
        return appointmentRepository.findByServiceProviderId(serviceProviderId);
    }

    @Override
    public List<Appointment> getUpcomingAppointments() {
        LocalDateTime now = LocalDateTime.now();
        List<Appointment.AppointmentStatus> activeStatuses = Arrays.asList(
                Appointment.AppointmentStatus.SCHEDULED,
                Appointment.AppointmentStatus.CONFIRMED
        );
        return appointmentRepository.findUpcomingAppointments(now, activeStatuses);
    }

    @Override
    public List<Appointment> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByAppointmentDateTimeBetween(start, end);
    }

    @Override
    public Appointment updateAppointmentStatus(Long id, Appointment.AppointmentStatus status) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    @Override
    public boolean hasTimeConflict(LocalDateTime dateTime, Long excludedAppointmentId) {
        List<Appointment.AppointmentStatus> conflictingStatuses = Arrays.asList(
                Appointment.AppointmentStatus.SCHEDULED,
                Appointment.AppointmentStatus.CONFIRMED
        );

        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(dateTime, conflictingStatuses);

        // Exclude current appointment from conflict check during update
        if (excludedAppointmentId != null) {
            conflicts.removeIf(appointment -> appointment.getId().equals(excludedAppointmentId));
        }

        return !conflicts.isEmpty();
    }

    @Override
    public boolean validateAppointmentOwnership(Long appointmentId, Long ownerId) {
        Appointment appointment = getAppointmentById(appointmentId);
        return appointment.getOwner().getId().equals(ownerId);
    }

    @Override
    public List<Appointment> getAppointmentsForManager() {
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment rescheduleAppointment(Long appointmentId, LocalDateTime newDateTime) {
        Appointment appointment = getAppointmentById(appointmentId);

        if (!appointment.canBeRescheduled()) {
            throw new RuntimeException("Appointment cannot be rescheduled");
        }

        // Check if new time is in the future
        if (newDateTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("New appointment time must be in the future");
        }

        // Check for time conflicts
        if (hasTimeConflict(newDateTime, appointmentId)) {
            throw new RuntimeException("Time conflict: Another appointment exists at this time");
        }

        // Check if service provider is available at new time
        if (appointment.getServiceProvider() != null &&
                !isTimeSlotAvailable(newDateTime, appointment.getServiceProvider().getId())) {
            throw new RuntimeException("Service provider is not available at the new time");
        }

        appointment.setAppointmentDateTime(newDateTime);
        appointment.setStatus(Appointment.AppointmentStatus.RESCHEDULED);

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment cancelAppointment(Long appointmentId, String reason) {
        Appointment appointment = getAppointmentById(appointmentId);

        if (!appointment.canBeCancelled()) {
            throw new RuntimeException("Appointment cannot be cancelled");
        }

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(reason);

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment confirmAppointmentCompletion(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);

        if (appointment.getAppointmentDateTime().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Cannot confirm completion of future appointments");
        }

        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getConflictingAppointments(LocalDateTime dateTime) {
        List<Appointment.AppointmentStatus> conflictingStatuses = Arrays.asList(
                Appointment.AppointmentStatus.PENDING,
                Appointment.AppointmentStatus.CONFIRMED
        );
        return appointmentRepository.findConflictingAppointments(dateTime, conflictingStatuses);
    }
    @Override
    public Appointment confirmPendingAppointment(Long appointmentId) {
        Appointment appointment = getAppointmentById(appointmentId);

        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING) {
            throw new RuntimeException("Only pending appointments can be confirmed.");
        }

        appointment.setStatus(Appointment.AppointmentStatus.SCHEDULED);
        return appointmentRepository.save(appointment);
    }


    @Override
    public Map<String, Long> getAppointmentStatistics() {
        Map<String, Long> stats = new HashMap<>();

        List<Appointment> allAppointments = appointmentRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        long total = allAppointments.size();
        long upcoming = allAppointments.stream()
                .filter(a -> a.getAppointmentDateTime().isAfter(now) &&
                        (a.getStatus() == Appointment.AppointmentStatus.SCHEDULED ||
                                a.getStatus() == Appointment.AppointmentStatus.CONFIRMED))
                .count();
        long completed = allAppointments.stream()
                .filter(a -> a.getStatus() == Appointment.AppointmentStatus.COMPLETED)
                .count();
        long cancelled = allAppointments.stream()
                .filter(a -> a.getStatus() == Appointment.AppointmentStatus.CANCELLED)
                .count();

        stats.put("total", total);
        stats.put("upcoming", upcoming);
        stats.put("completed", completed);
        stats.put("cancelled", cancelled);

        return stats;
    }

    @Override
    public List<Appointment> getTodaysAppointments() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return appointmentRepository.findByAppointmentDateTimeBetween(startOfDay, endOfDay);
    }

    @Override
    public List<AvailableSlotDTO> getAvailableTimeSlots(LocalDate date, Appointment.AppointmentType serviceType) {
        List<AvailableSlotDTO> availableSlots = new ArrayList<>();
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        // Get service providers for the specified service type
        List<ServiceProvider> providers = serviceProviderRepository.findBySpecialization(serviceType.name());

        for (ServiceProvider provider : providers) {
            if (provider.isAvailableOn(dayOfWeek)) {
                availableSlots.addAll(generateSlotsForProvider(provider, date));
            }
        }

        return availableSlots;
    }

    @Override
    public List<AvailableSlotDTO> getAvailableSlotsForProvider(Long providerId, LocalDate date) {
        ServiceProvider provider = serviceProviderRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("Service provider not found with id: " + providerId));

        DayOfWeek dayOfWeek = date.getDayOfWeek();

        if (!provider.isAvailableOn(dayOfWeek)) {
            return Collections.emptyList();
        }

        return generateSlotsForProvider(provider, date);
    }

    @Override
    public boolean isTimeSlotAvailable(LocalDateTime dateTime, Long serviceProviderId) {
        ServiceProvider provider = serviceProviderRepository.findById(serviceProviderId)
                .orElseThrow(() -> new RuntimeException("Service provider not found with id: " + serviceProviderId));

        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        LocalTime time = dateTime.toLocalTime();

        // Check if provider works on this day and time
        if (!provider.isAvailableOn(dayOfWeek) || !provider.isWithinWorkingHours(time)) {
            return false;
        }

        // Check for existing appointments at this time
        List<Appointment.AppointmentStatus> conflictingStatuses = Arrays.asList(
                Appointment.AppointmentStatus.SCHEDULED,
                Appointment.AppointmentStatus.CONFIRMED
        );

        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(dateTime, conflictingStatuses);

        // Filter conflicts for this specific provider
        conflicts = conflicts.stream()
                .filter(appointment -> appointment.getServiceProvider() != null &&
                        appointment.getServiceProvider().getId().equals(serviceProviderId))
                .toList();

        return conflicts.isEmpty();
    }

    private List<AvailableSlotDTO> generateSlotsForProvider(ServiceProvider provider, LocalDate date) {
        List<AvailableSlotDTO> slots = new ArrayList<>();
        LocalTime startTime = provider.getStartTime();
        LocalTime endTime = provider.getEndTime();
        int slotDuration = provider.getSlotDuration();

        LocalTime current = startTime;
        while (current.plusMinutes(slotDuration).isBefore(endTime) ||
                current.plusMinutes(slotDuration).equals(endTime)) {

            LocalDateTime slotStart = LocalDateTime.of(date, current);
            LocalDateTime slotEnd = slotStart.plusMinutes(slotDuration);

            // Check if slot is available
            if (isTimeSlotAvailable(slotStart, provider.getId())) {
                slots.add(new AvailableSlotDTO(
                        slotStart,
                        slotEnd,
                        provider.getId(),
                        provider.getName(),
                        provider.getSpecialization()
                ));
            }

            current = current.plusMinutes(slotDuration);
        }

        return slots;
    }
}
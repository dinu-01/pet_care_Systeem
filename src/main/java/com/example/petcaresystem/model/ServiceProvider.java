package com.example.petcaresystem.model;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "service_providers")
public class ServiceProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String specialization; // VETERINARY, GROOMING, VACCINATION

    private String email;
    private String phone;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ElementCollection
    @CollectionTable(name = "provider_availability", joinColumns = @JoinColumn(name = "provider_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "available_day")
    private Set<DayOfWeek> availableDays;

    private LocalTime startTime;
    private LocalTime endTime;

    private Integer slotDuration = 30; // minutes

    // Constructors
    public ServiceProvider() {}

    public ServiceProvider(String name, String specialization, String email, String phone,
                           User user, Set<DayOfWeek> availableDays, LocalTime startTime, LocalTime endTime) {
        this.name = name;
        this.specialization = specialization;
        this.email = email;
        this.phone = phone;
        this.user = user;
        this.availableDays = availableDays;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Set<DayOfWeek> getAvailableDays() { return availableDays; }
    public void setAvailableDays(Set<DayOfWeek> availableDays) { this.availableDays = availableDays; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Integer getSlotDuration() { return slotDuration; }
    public void setSlotDuration(Integer slotDuration) { this.slotDuration = slotDuration; }

    // Helper methods
    public boolean isAvailableOn(DayOfWeek day) {
        return availableDays.contains(day);
    }

    public boolean isWithinWorkingHours(LocalTime time) {
        return !time.isBefore(startTime) && !time.isAfter(endTime);
    }
}
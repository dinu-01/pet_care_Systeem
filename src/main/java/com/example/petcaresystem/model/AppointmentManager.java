package com.example.petcaresystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "appointment_managers")
public class AppointmentManager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    // Relationship with User
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Constructors
    public AppointmentManager() {}

    public AppointmentManager(String name, String email, User user) {
        this.name = name;
        this.email = email;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
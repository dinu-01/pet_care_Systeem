package com.example.petcaresystem.dto;

import java.time.LocalDate;

public class PetDTO {
    private String name;
    private String breed;
    private String color;
    private String species;
    private LocalDate dob;
    private Long ownerId;

    // Constructors
    public PetDTO() {}

    public PetDTO(String name, String breed, String color, String species, LocalDate dob, Long ownerId) {
        this.name = name;
        this.breed = breed;
        this.color = color;
        this.species = species;
        this.dob = dob;
        this.ownerId = ownerId;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
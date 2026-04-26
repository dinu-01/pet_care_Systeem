package com.example.petcaresystem.service;

import com.example.petcaresystem.model.Pet;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface PetService {

    // Basic CRUD operations
    Pet createPet(Pet pet, Long ownerId);
    Pet createPetWithImage(Pet pet, Long ownerId, MultipartFile imageFile) throws IOException;
    List<Pet> getAllPets();
    List<Pet> getAllActivePets();
    Pet getPetById(Long id);
    Pet getPetByIdAndOwner(Long id, Long ownerId);
    List<Pet> getPetsByOwner(Long ownerId);
    List<Pet> getActivePetsByOwner(Long ownerId);
    List<Pet> getPetsBySpecies(String species);
    Pet updatePet(Long id, Pet petDetails);
    Pet updatePetWithImage(Long id, Pet petDetails, MultipartFile imageFile) throws IOException;
    void deletePet(Long id);
    void softDeletePet(Long id);

    // Search and filter operations
    List<Pet> searchPetsByName(String name, Long ownerId);
    List<Pet> searchActivePetsByName(String name, Long ownerId);

    // Utility operations
    Integer calculateAge(java.time.LocalDate dateOfBirth);
    boolean existsById(Long id);
    boolean existsByNameAndOwner(String name, Long ownerId);
    long countPetsByOwner(Long ownerId);
    long countActivePetsByOwner(Long ownerId);

    // Image operations
    String storePetImage(MultipartFile imageFile) throws IOException;
    void deletePetImage(String imageName) throws IOException;
    byte[] getPetImageBytes(String fileName) throws IOException; // ✅ NEW METHOD

    // Additional business operations
    Pet deactivatePet(Long id);
    Pet activatePet(Long id);
    List<Pet> getPetsByBreed(String breed);
    Optional<Pet> getPetByMicrochipId(String microchipId);
}
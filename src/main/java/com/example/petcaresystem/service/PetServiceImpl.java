package com.example.petcaresystem.service;

import com.example.petcaresystem.model.Pet;
import com.example.petcaresystem.model.User;
import com.example.petcaresystem.repo.PetRepository;
import com.example.petcaresystem.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Override
    public Pet createPet(Pet pet, Long ownerId) {
        try {
            log.info("Creating new pet: {} for owner ID: {}", pet.getName(), ownerId);

            validatePetData(pet);

            // Find owner
            User owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> new RuntimeException("Pet owner not found with id: " + ownerId));

            // Check if pet name already exists for this owner
            if (petRepository.existsByNameAndOwnerId(pet.getName(), ownerId)) {
                throw new RuntimeException("Pet with name '" + pet.getName() + "' already exists for this owner");
            }

            // Set owner and timestamps
            pet.setOwner(owner);
            pet.setCreatedAt(LocalDateTime.now());
            pet.setUpdatedAt(LocalDateTime.now());
            pet.setIsActive(true);

            // Calculate age if date of birth is provided
            if (pet.getDateOfBirth() != null) {
                Integer age = calculateAge(pet.getDateOfBirth());
                pet.setAge(age);
            }

            // Validate microchip ID uniqueness if provided
            if (pet.getMicrochipId() != null && !pet.getMicrochipId().trim().isEmpty()) {
                if (petRepository.existsByMicrochipId(pet.getMicrochipId())) {
                    throw new RuntimeException("Microchip ID '" + pet.getMicrochipId() + "' already exists");
                }
            }

            Pet savedPet = petRepository.save(pet);
            log.info("Pet created successfully with ID: {}", savedPet.getId());
            return savedPet;

        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating pet: {}", e.getMessage());
            throw new RuntimeException("Database constraint violation: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error creating pet: {}", e.getMessage());
            throw new RuntimeException("Error creating pet: " + e.getMessage());
        }
    }

    @Override
    public Pet createPetWithImage(Pet pet, Long ownerId, MultipartFile imageFile) throws IOException {
        try {
            log.info("Creating new pet with image: {} for owner ID: {}", pet.getName(), ownerId);

            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = fileStorageService.storeFile(imageFile);
                pet.setImageName(fileName);
                pet.setImageUrl(fileStorageService.getFileUrl(fileName)); // ✅ Set proper image URL
                log.info("Image stored: {} with URL: {}", fileName, pet.getImageUrl());
            }

            return createPet(pet, ownerId);

        } catch (Exception e) {
            // Delete uploaded image if pet creation fails
            if (pet.getImageName() != null) {
                try {
                    fileStorageService.deleteFile(pet.getImageName());
                } catch (IOException ioException) {
                    log.warn("Failed to delete image after pet creation failure: {}", ioException.getMessage());
                }
            }
            throw new RuntimeException("Error creating pet with image: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pet> getAllPets() {
        try {
            log.info("Retrieving all pets");
            return petRepository.findAll();
        } catch (Exception e) {
            log.error("Error retrieving all pets: {}", e.getMessage());
            throw new RuntimeException("Error retrieving pets: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pet> getAllActivePets() {
        try {
            log.info("Retrieving all active pets");
            return petRepository.findByIsActiveTrue();
        } catch (Exception e) {
            log.error("Error retrieving active pets: {}", e.getMessage());
            throw new RuntimeException("Error retrieving active pets: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Pet getPetById(Long id) {
        try {
            log.info("Retrieving pet by ID: {}", id);
            return petRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pet not found with id: " + id));
        } catch (Exception e) {
            log.error("Error retrieving pet by id {}: {}", id, e.getMessage());
            throw new RuntimeException("Error retrieving pet: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Pet getPetByIdAndOwner(Long id, Long ownerId) {
        try {
            log.info("Retrieving pet by ID: {} for owner ID: {}", id, ownerId);
            return (Pet) petRepository.findByIdAndOwnerId(id, ownerId)
                    .orElseThrow(() -> new RuntimeException("Pet not found with id: " + id + " for owner: " + ownerId));
        } catch (Exception e) {
            log.error("Error retrieving pet {} for owner {}: {}", id, ownerId, e.getMessage());
            throw new RuntimeException("Error retrieving pet: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pet> getPetsByOwner(Long ownerId) {
        try {
            log.info("Retrieving all pets for owner ID: {}", ownerId);

            // Verify owner exists
            if (!userRepository.existsById(ownerId)) {
                throw new RuntimeException("Owner not found with id: " + ownerId);
            }

            List<Pet> pets = petRepository.findByOwnerId(ownerId);

            // ✅ Ensure image URLs are properly set
            pets.forEach(pet -> {
                if (pet.getImageName() != null && !pet.getImageName().isEmpty() &&
                        (pet.getImageUrl() == null || pet.getImageUrl().isEmpty())) {
                    pet.setImageUrl(fileStorageService.getFileUrl(pet.getImageName()));
                }
            });

            log.info("Found {} pets for owner ID: {}", pets.size(), ownerId);
            return pets;

        } catch (Exception e) {
            log.error("Error retrieving pets for owner {}: {}", ownerId, e.getMessage());
            throw new RuntimeException("Error retrieving pets: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pet> getActivePetsByOwner(Long ownerId) {
        try {
            log.info("Retrieving active pets for owner ID: {}", ownerId);

            if (!userRepository.existsById(ownerId)) {
                throw new RuntimeException("Owner not found with id: " + ownerId);
            }

            List<Pet> pets = petRepository.findByOwnerIdAndIsActiveTrue(ownerId);

            // ✅ Ensure image URLs are properly set
            pets.forEach(pet -> {
                if (pet.getImageName() != null && !pet.getImageName().isEmpty() &&
                        (pet.getImageUrl() == null || pet.getImageUrl().isEmpty())) {
                    pet.setImageUrl(fileStorageService.getFileUrl(pet.getImageName()));
                }
            });

            log.info("Found {} active pets for owner ID: {}", pets.size(), ownerId);
            return pets;

        } catch (Exception e) {
            log.error("Error retrieving active pets for owner {}: {}", ownerId, e.getMessage());
            throw new RuntimeException("Error retrieving active pets: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pet> getPetsBySpecies(String species) {
        try {
            log.info("Retrieving pets by species: {}", species);

            if (species == null || species.trim().isEmpty()) {
                throw new IllegalArgumentException("Species cannot be null or empty");
            }

            List<Pet> pets = petRepository.findBySpecies(species);
            log.info("Found {} pets with species: {}", pets.size(), species);
            return pets;

        } catch (Exception e) {
            log.error("Error retrieving pets by species {}: {}", species, e.getMessage());
            throw new RuntimeException("Error retrieving pets by species: " + e.getMessage());
        }
    }

    @Override
    public Pet updatePet(Long id, Pet petDetails) {
        try {
            log.info("Updating pet with ID: {}", id);

            validatePetData(petDetails);

            Pet existingPet = petRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pet not found with id: " + id));

            // Check if new name conflicts with other pets of the same owner
            if (!existingPet.getName().equals(petDetails.getName()) &&
                    petRepository.existsByNameAndOwnerId(petDetails.getName(), existingPet.getOwner().getId())) {
                throw new RuntimeException("Pet with name '" + petDetails.getName() + "' already exists for this owner");
            }

            // Check microchip ID uniqueness if changed
            if (petDetails.getMicrochipId() != null &&
                    !petDetails.getMicrochipId().equals(existingPet.getMicrochipId()) &&
                    petRepository.existsByMicrochipId(petDetails.getMicrochipId())) {
                throw new RuntimeException("Microchip ID '" + petDetails.getMicrochipId() + "' already exists");
            }

            // Update fields
            updatePetFields(existingPet, petDetails);

            Pet updatedPet = petRepository.save(existingPet);
            log.info("Pet updated successfully: {}", updatedPet.getId());
            return updatedPet;

        } catch (Exception e) {
            log.error("Error updating pet with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error updating pet: " + e.getMessage());
        }
    }

    @Override
    public Pet updatePetWithImage(Long id, Pet petDetails, MultipartFile imageFile) throws IOException {
        try {
            log.info("Updating pet with image for ID: {}", id);

            Pet existingPet = petRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pet not found with id: " + id));

            // Handle image update
            if (imageFile != null && !imageFile.isEmpty()) {
                // Delete old image if exists
                if (existingPet.getImageName() != null) {
                    try {
                        fileStorageService.deleteFile(existingPet.getImageName());
                    } catch (IOException e) {
                        log.warn("Failed to delete old image: {}", e.getMessage());
                    }
                }

                String fileName = fileStorageService.storeFile(imageFile);
                existingPet.setImageName(fileName);
                existingPet.setImageUrl(fileStorageService.getFileUrl(fileName)); // ✅ Set proper URL
                log.info("New image stored: {} with URL: {}", fileName, existingPet.getImageUrl());
            }

            // Update other fields
            updatePetFields(existingPet, petDetails);

            Pet updatedPet = petRepository.save(existingPet);
            log.info("Pet updated successfully with image: {}", updatedPet.getId());
            return updatedPet;

        } catch (Exception e) {
            log.error("Error updating pet with image for ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error updating pet with image: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void deletePet(Long id) {
        try {
            log.info("Hard deleting pet with ID: {}", id);

            Pet pet = petRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pet not found with id: " + id));

            // Delete associated image file
            if (pet.getImageName() != null) {
                try {
                    fileStorageService.deleteFile(pet.getImageName());
                } catch (IOException e) {
                    log.warn("Failed to delete pet image: {}", e.getMessage());
                }
            }

            petRepository.delete(pet);

            // Verify deletion
            if (petRepository.existsById(id)) {
                throw new RuntimeException("Pet deletion failed - pet still exists");
            }

            log.info("Pet hard deleted successfully: {}", id);

        } catch (DataIntegrityViolationException e) {
            log.error("Cannot delete pet with ID {} due to foreign key constraints: {}", id, e.getMessage());
            throw new RuntimeException("Cannot delete pet. It may have related appointments or medical records. " +
                    "Please use soft delete instead.");
        } catch (Exception e) {
            log.error("Error deleting pet with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error deleting pet: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void softDeletePet(Long id) {
        try {
            log.info("Soft deleting pet with ID: {}", id);

            Pet pet = petRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pet not found with id: " + id));

            pet.setIsActive(false);
            pet.setUpdatedAt(LocalDateTime.now());

            petRepository.save(pet);
            log.info("Pet soft deleted successfully: {}", id);

        } catch (Exception e) {
            log.error("Error soft deleting pet with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error soft deleting pet: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pet> searchPetsByName(String name, Long ownerId) {
        try {
            log.info("Searching pets by name: {} for owner ID: {}", name, ownerId);

            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Search name cannot be null or empty");
            }

            List<Pet> pets = petRepository.findByNameContainingIgnoreCaseAndOwnerId(name.trim(), ownerId);
            log.info("Found {} pets matching search: {}", pets.size(), name);
            return pets;

        } catch (Exception e) {
            log.error("Error searching pets by name {} for owner {}: {}", name, ownerId, e.getMessage());
            throw new RuntimeException("Error searching pets: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pet> searchActivePetsByName(String name, Long ownerId) {
        try {
            log.info("Searching active pets by name: {} for owner ID: {}", name, ownerId);

            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Search name cannot be null or empty");
            }

            List<Pet> pets = petRepository.findByNameContainingIgnoreCaseAndOwnerIdAndIsActiveTrue(name.trim(), ownerId);
            log.info("Found {} active pets matching search: {}", pets.size(), name);
            return pets;

        } catch (Exception e) {
            log.error("Error searching active pets by name {} for owner {}: {}", name, ownerId, e.getMessage());
            throw new RuntimeException("Error searching active pets: " + e.getMessage());
        }
    }

    @Override
    public Integer calculateAge(LocalDate dateOfBirth) {
        try {
            if (dateOfBirth == null) {
                return null;
            }

            LocalDate currentDate = LocalDate.now();

            if (dateOfBirth.isAfter(currentDate)) {
                log.warn("Date of birth {} is in the future, returning age 0", dateOfBirth);
                return 0;
            }

            int age = java.time.Period.between(dateOfBirth, currentDate).getYears();
            log.debug("Calculated age {} for date of birth {}", age, dateOfBirth);
            return age;

        } catch (Exception e) {
            log.error("Error calculating age for date of birth {}: {}", dateOfBirth, e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        try {
            return petRepository.existsById(id);
        } catch (Exception e) {
            log.error("Error checking pet existence for ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error checking pet existence: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndOwner(String name, Long ownerId) {
        try {
            return petRepository.existsByNameAndOwnerId(name, ownerId);
        } catch (Exception e) {
            log.error("Error checking pet name existence {} for owner {}: {}", name, ownerId, e.getMessage());
            throw new RuntimeException("Error checking pet name existence: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countPetsByOwner(Long ownerId) {
        try {
            log.info("Counting pets for owner ID: {}", ownerId);

            if (!userRepository.existsById(ownerId)) {
                throw new RuntimeException("Owner not found with id: " + ownerId);
            }

            long count = petRepository.countByOwnerId(ownerId);
            log.info("Found {} pets for owner ID: {}", count, ownerId);
            return count;

        } catch (Exception e) {
            log.error("Error counting pets for owner {}: {}", ownerId, e.getMessage());
            throw new RuntimeException("Error counting pets: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countActivePetsByOwner(Long ownerId) {
        try {
            log.info("Counting active pets for owner ID: {}", ownerId);

            if (!userRepository.existsById(ownerId)) {
                throw new RuntimeException("Owner not found with id: " + ownerId);
            }

            long count = petRepository.countByOwnerIdAndIsActiveTrue(ownerId);
            log.info("Found {} active pets for owner ID: {}", count, ownerId);
            return count;

        } catch (Exception e) {
            log.error("Error counting active pets for owner {}: {}", ownerId, e.getMessage());
            throw new RuntimeException("Error counting active pets: " + e.getMessage());
        }
    }

    @Override
    public String storePetImage(MultipartFile imageFile) throws IOException {
        try {
            return fileStorageService.storeFile(imageFile);
        } catch (Exception e) {
            log.error("Error storing pet image: {}", e.getMessage());
            throw new IOException("Error storing pet image: " + e.getMessage());
        }
    }

    @Override
    public void deletePetImage(String imageName) throws IOException {
        try {
            fileStorageService.deleteFile(imageName);
        } catch (Exception e) {
            log.error("Error deleting pet image {}: {}", imageName, e.getMessage());
            throw new IOException("Error deleting pet image: " + e.getMessage());
        }
    }

    @Override
    public byte[] getPetImageBytes(String fileName) throws IOException {
        try {
            return fileStorageService.loadFile(fileName);
        } catch (Exception e) {
            log.error("Error loading pet image {}: {}", fileName, e.getMessage());
            throw new IOException("Error loading pet image: " + e.getMessage());
        }
    }

    @Override
    public Pet deactivatePet(Long id) {
        try {
            log.info("Deactivating pet with ID: {}", id);

            Pet pet = petRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pet not found with id: " + id));

            pet.setIsActive(false);
            pet.setUpdatedAt(LocalDateTime.now());

            Pet deactivatedPet = petRepository.save(pet);
            log.info("Pet deactivated successfully: {}", id);
            return deactivatedPet;

        } catch (Exception e) {
            log.error("Error deactivating pet with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error deactivating pet: " + e.getMessage());
        }
    }

    @Override
    public Pet activatePet(Long id) {
        try {
            log.info("Activating pet with ID: {}", id);

            Pet pet = petRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pet not found with id: " + id));

            pet.setIsActive(true);
            pet.setUpdatedAt(LocalDateTime.now());

            Pet activatedPet = petRepository.save(pet);
            log.info("Pet activated successfully: {}", id);
            return activatedPet;

        } catch (Exception e) {
            log.error("Error activating pet with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error activating pet: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pet> getPetsByBreed(String breed) {
        try {
            log.info("Retrieving pets by breed: {}", breed);

            if (breed == null || breed.trim().isEmpty()) {
                throw new IllegalArgumentException("Breed cannot be null or empty");
            }

            List<Pet> pets = petRepository.findByBreed(breed);
            log.info("Found {} pets with breed: {}", pets.size(), breed);
            return pets;

        } catch (Exception e) {
            log.error("Error retrieving pets by breed {}: {}", breed, e.getMessage());
            throw new RuntimeException("Error retrieving pets by breed: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pet> getPetByMicrochipId(String microchipId) {
        try {
            log.info("Retrieving pet by microchip ID: {}", microchipId);

            if (microchipId == null || microchipId.trim().isEmpty()) {
                throw new IllegalArgumentException("Microchip ID cannot be null or empty");
            }

            return petRepository.findByMicrochipId(microchipId.trim());

        } catch (Exception e) {
            log.error("Error retrieving pet by microchip ID {}: {}", microchipId, e.getMessage());
            throw new RuntimeException("Error retrieving pet by microchip ID: " + e.getMessage());
        }
    }

    // Private helper methods
    private void validatePetData(Pet pet) {
        if (pet.getName() == null || pet.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Pet name is required");
        }
        if (pet.getSpecies() == null || pet.getSpecies().trim().isEmpty()) {
            throw new IllegalArgumentException("Pet species is required");
        }
    }

    private void updatePetFields(Pet existingPet, Pet petDetails) {
        existingPet.setName(petDetails.getName());
        existingPet.setSpecies(petDetails.getSpecies());
        existingPet.setBreed(petDetails.getBreed());
        existingPet.setColor(petDetails.getColor());
        existingPet.setGender(petDetails.getGender());
        existingPet.setWeight(petDetails.getWeight());
        existingPet.setMedicalNotes(petDetails.getMedicalNotes());
        existingPet.setMicrochipId(petDetails.getMicrochipId());

        // Update date of birth and recalculate age if changed
        if (petDetails.getDateOfBirth() != null &&
                !petDetails.getDateOfBirth().equals(existingPet.getDateOfBirth())) {
            existingPet.setDateOfBirth(petDetails.getDateOfBirth());
            Integer age = calculateAge(petDetails.getDateOfBirth());
            existingPet.setAge(age);
        }

        existingPet.setUpdatedAt(LocalDateTime.now());
    }
}
package com.example.petcaresystem.controller;

import com.example.petcaresystem.model.Pet;
import com.example.petcaresystem.service.PetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/pets")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class PetController {

    private final PetService petService;

    // ---------------- HEALTH CHECK ----------------
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("✅ Pet Management API is healthy!");
    }

    // ---------------- CREATE ----------------
    @PostMapping
    public ResponseEntity<?> createPet(@RequestBody PetCreateRequest request) {
        try {
            log.info("Creating new pet: {} for owner: {}", request.getName(), request.getOwnerId());
            Pet pet = new Pet();
            mapRequestToPet(request, pet);
            Pet created = petService.createPet(pet, request.getOwnerId());
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("❌ Error creating pet: {}", e.getMessage());
            return error("Error creating pet: " + e.getMessage());
        }
    }

    @PostMapping(value = "/with-image", consumes = "multipart/form-data")
    public ResponseEntity<?> createPetWithImage(
            @RequestParam("name") String name,
            @RequestParam("species") String species,
            @RequestParam("ownerId") Long ownerId,
            @RequestParam(value = "breed", required = false) String breed,
            @RequestParam(value = "dateOfBirth", required = false) String dateOfBirth,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "weight", required = false) Double weight,
            @RequestParam(value = "color", required = false) String color,
            @RequestParam(value = "medicalNotes", required = false) String medicalNotes,
            @RequestParam(value = "microchipId", required = false) String microchipId,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        try {
            Pet pet = new Pet();
            pet.setName(name);
            pet.setSpecies(species);
            pet.setBreed(breed);
            pet.setGender(gender);
            pet.setWeight(weight);
            pet.setColor(color);
            pet.setMedicalNotes(medicalNotes);
            pet.setMicrochipId(microchipId);

            if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
                pet.setDateOfBirth(java.time.LocalDate.parse(dateOfBirth));
            }

            Pet created = petService.createPetWithImage(pet, ownerId, imageFile);
            log.info("✅ Pet created successfully with ID {}", created.getId());
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("❌ Error creating pet with image: {}", e.getMessage());
            return error("Error creating pet: " + e.getMessage());
        }
    }

    // ---------------- READ ----------------
    @GetMapping
    public ResponseEntity<?> getAllPets() {
        try {
            return ResponseEntity.ok(petService.getAllPets());
        } catch (Exception e) {
            return error("Error retrieving pets: " + e.getMessage());
        }
    }

    @GetMapping("/active")
    public ResponseEntity<?> getAllActivePets() {
        try {
            return ResponseEntity.ok(petService.getAllActivePets());
        } catch (Exception e) {
            return error("Error retrieving active pets: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPetById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(petService.getPetById(id));
        } catch (Exception e) {
            return error("Pet not found: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/owner/{ownerId}")
    public ResponseEntity<?> getPetByIdAndOwner(@PathVariable Long id, @PathVariable Long ownerId) {
        try {
            return ResponseEntity.ok(petService.getPetByIdAndOwner(id, ownerId));
        } catch (Exception e) {
            return error("Pet not found: " + e.getMessage());
        }
    }

    // ✅ FIXED IMAGE URL METHOD
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<?> getPetsByOwner(@PathVariable Long ownerId) {
        try {
            List<Pet> pets = petService.getPetsByOwner(ownerId);
            if (pets.isEmpty()) return ResponseEntity.noContent().build();

            // Map to lightweight JSON with proper image URL handling
            List<Map<String, Object>> petList = pets.stream().map(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getId());
                map.put("name", p.getName());
                map.put("species", p.getSpecies());
                map.put("breed", p.getBreed());
                map.put("color", p.getColor());
                map.put("gender", p.getGender());
                map.put("weight", p.getWeight());
                map.put("dateOfBirth", p.getDateOfBirth());
                map.put("medicalNotes", p.getMedicalNotes());
                map.put("microchipId", p.getMicrochipId());
                map.put("isActive", p.getIsActive());

                // ✅ FIXED: Proper image URL handling
                if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
                    map.put("imageUrl", p.getImageUrl());
                } else {
                    map.put("imageUrl", null);
                }

                // ✅ Include image name for reference
                map.put("imageName", p.getImageName());

                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(petList);
        } catch (Exception e) {
            log.error("❌ Error retrieving pets for owner {}: {}", ownerId, e.getMessage());
            return error("Error retrieving pets: " + e.getMessage());
        }
    }

    @GetMapping("/owner/{ownerId}/active")
    public ResponseEntity<?> getActivePetsByOwner(@PathVariable Long ownerId) {
        try {
            List<Pet> pets = petService.getActivePetsByOwner(ownerId);
            if (pets.isEmpty()) return ResponseEntity.noContent().build();

            // Map to lightweight JSON with image URL
            List<Map<String, Object>> petList = pets.stream().map(p -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", p.getId());
                map.put("name", p.getName());
                map.put("species", p.getSpecies());
                map.put("breed", p.getBreed());
                map.put("color", p.getColor());

                // ✅ FIXED: Image URL handling
                if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
                    map.put("imageUrl", p.getImageUrl());
                } else {
                    map.put("imageUrl", null);
                }

                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(petList);
        } catch (Exception e) {
            return error("Error retrieving active pets: " + e.getMessage());
        }
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePet(@PathVariable Long id, @RequestBody PetUpdateRequest req) {
        try {
            Pet p = new Pet();
            p.setName(req.getName());
            p.setSpecies(req.getSpecies());
            p.setBreed(req.getBreed());
            p.setGender(req.getGender());
            p.setColor(req.getColor());
            p.setWeight(req.getWeight());
            p.setMedicalNotes(req.getMedicalNotes());
            p.setMicrochipId(req.getMicrochipId());
            if (req.getDateOfBirth() != null) p.setDateOfBirth(req.getDateOfBirth());
            return ResponseEntity.ok(petService.updatePet(id, p));
        } catch (Exception e) {
            return error("Error updating pet: " + e.getMessage());
        }
    }

    // ✅ FIXED UPDATE WITH IMAGE
    @PutMapping(value = "/{id}/with-image", consumes = "multipart/form-data")
    public ResponseEntity<?> updatePetWithImage(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("species") String species,
            @RequestParam(value = "breed", required = false) String breed,
            @RequestParam(value = "dateOfBirth", required = false) String dateOfBirth,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "weight", required = false) Double weight,
            @RequestParam(value = "color", required = false) String color,
            @RequestParam(value = "medicalNotes", required = false) String medicalNotes,
            @RequestParam(value = "microchipId", required = false) String microchipId,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        try {
            log.info("🔄 Updating pet [{}] with image: {}", id,
                    imageFile != null ? imageFile.getOriginalFilename() : "none");

            Pet pet = new Pet();
            pet.setName(name);
            pet.setSpecies(species);
            pet.setBreed(breed);
            pet.setGender(gender);
            pet.setWeight(weight);
            pet.setColor(color);
            pet.setMedicalNotes(medicalNotes);
            pet.setMicrochipId(microchipId);

            if (dateOfBirth != null && !dateOfBirth.trim().isEmpty()) {
                pet.setDateOfBirth(java.time.LocalDate.parse(dateOfBirth));
            }

            Pet updated = petService.updatePetWithImage(id, pet, imageFile);
            log.info("✅ Pet [{}] updated successfully!", updated.getId());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("❌ Error updating pet [{}]: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Error updating pet with image: " + e.getMessage()));
        }
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePet(@PathVariable Long id) {
        try {
            petService.deletePet(id);
            return success("Pet deleted successfully");
        } catch (Exception e) {
            return error("Error deleting pet: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}/soft")
    public ResponseEntity<?> softDeletePet(@PathVariable Long id) {
        try {
            petService.softDeletePet(id);
            return success("Pet deactivated successfully");
        } catch (Exception e) {
            return error("Error deactivating pet: " + e.getMessage());
        }
    }

    // ---------------- SEARCH / COUNT / STATUS ----------------
    @GetMapping("/search")
    public ResponseEntity<?> searchPetsByName(@RequestParam("name") String name,
                                              @RequestParam("ownerId") Long ownerId) {
        try {
            return ResponseEntity.ok(petService.searchPetsByName(name, ownerId));
        } catch (Exception e) {
            return error("Error searching pets: " + e.getMessage());
        }
    }

    @GetMapping("/count/owner/{ownerId}")
    public ResponseEntity<?> countPetsByOwner(@PathVariable Long ownerId) {
        try {
            return ResponseEntity.ok(Map.of("count", petService.countPetsByOwner(ownerId)));
        } catch (Exception e) {
            return error("Error counting pets: " + e.getMessage());
        }
    }

    @GetMapping("/count/owner/{ownerId}/active")
    public ResponseEntity<?> countActivePetsByOwner(@PathVariable Long ownerId) {
        try {
            return ResponseEntity.ok(Map.of("count", petService.countActivePetsByOwner(ownerId)));
        } catch (Exception e) {
            return error("Error counting active pets: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activatePet(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(petService.activatePet(id));
        } catch (Exception e) {
            return error("Error activating pet: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivatePet(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(petService.deactivatePet(id));
        } catch (Exception e) {
            return error("Error deactivating pet: " + e.getMessage());
        }
    }

    // ---------------- MICROCHIP ----------------
    @GetMapping("/microchip/{microchipId}")
    public ResponseEntity<?> getPetByMicrochipId(@PathVariable String microchipId) {
        try {
            return petService.getPetByMicrochipId(microchipId)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> error("Pet not found with microchip ID: " + microchipId));
        } catch (Exception e) {
            return error("Error retrieving pet: " + e.getMessage());
        }
    }

    // ---------------- IMAGE ACCESS ----------------
    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<byte[]> getPetImage(@PathVariable String filename) {
        try {
            byte[] imageBytes = petService.getPetImageBytes(filename);
            return ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.IMAGE_JPEG)
                    .body(imageBytes);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ---------------- HELPER METHODS ----------------
    private void mapRequestToPet(PetCreateRequest req, Pet pet) {
        pet.setName(req.getName());
        pet.setSpecies(req.getSpecies());
        pet.setBreed(req.getBreed());
        pet.setDateOfBirth(req.getDateOfBirth());
        pet.setGender(req.getGender());
        pet.setWeight(req.getWeight());
        pet.setColor(req.getColor());
        pet.setMedicalNotes(req.getMedicalNotes());
        pet.setMicrochipId(req.getMicrochipId());
    }

    private ResponseEntity<Map<String, String>> error(String msg) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", msg));
    }

    private ResponseEntity<Map<String, String>> success(String msg) {
        return ResponseEntity.ok(Map.of("message", msg));
    }

    // ---------------- DTOs ----------------
    public static class PetCreateRequest {
        private String name;
        private String species;
        private String breed;
        private java.time.LocalDate dateOfBirth;
        private String gender;
        private Double weight;
        private String color;
        private String medicalNotes;
        private String microchipId;
        private Long ownerId;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String n) { name = n; }
        public String getSpecies() { return species; }
        public void setSpecies(String s) { species = s; }
        public String getBreed() { return breed; }
        public void setBreed(String b) { breed = b; }
        public java.time.LocalDate getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(java.time.LocalDate d) { dateOfBirth = d; }
        public String getGender() { return gender; }
        public void setGender(String g) { gender = g; }
        public Double getWeight() { return weight; }
        public void setWeight(Double w) { weight = w; }
        public String getColor() { return color; }
        public void setColor(String c) { color = c; }
        public String getMedicalNotes() { return medicalNotes; }
        public void setMedicalNotes(String m) { medicalNotes = m; }
        public String getMicrochipId() { return microchipId; }
        public void setMicrochipId(String m) { microchipId = m; }
        public Long getOwnerId() { return ownerId; }
        public void setOwnerId(Long o) { ownerId = o; }
    }

    public static class PetUpdateRequest {
        private String name;
        private String species;
        private String breed;
        private java.time.LocalDate dateOfBirth;
        private String gender;
        private Double weight;
        private String color;
        private String medicalNotes;
        private String microchipId;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String n) { name = n; }
        public String getSpecies() { return species; }
        public void setSpecies(String s) { species = s; }
        public String getBreed() { return breed; }
        public void setBreed(String b) { breed = b; }
        public java.time.LocalDate getDateOfBirth() { return dateOfBirth; }
        public void setDateOfBirth(java.time.LocalDate d) { dateOfBirth = d; }
        public String getGender() { return gender; }
        public void setGender(String g) { gender = g; }
        public Double getWeight() { return weight; }
        public void setWeight(Double w) { weight = w; }
        public String getColor() { return color; }
        public void setColor(String c) { color = c; }
        public String getMedicalNotes() { return medicalNotes; }
        public void setMedicalNotes(String m) { medicalNotes = m; }
        public String getMicrochipId() { return microchipId; }
        public void setMicrochipId(String m) { microchipId = m; }
    }
}
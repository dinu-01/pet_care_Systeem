package com.example.petcaresystem.repo;

import com.example.petcaresystem.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    // 🐾 All pets by owner
    List<Pet> findByOwner_Id(Long ownerId);


    List<Pet> findByOwner_IdAndIsActiveTrue(Long ownerId);
    List<Pet> findByNameContainingIgnoreCaseAndOwner_Id(String name, Long ownerId);
    List<Pet> findByNameContainingIgnoreCaseAndOwner_IdAndIsActiveTrue(String name, Long ownerId);
    long countByOwner_Id(Long ownerId);
    long countByOwner_IdAndIsActiveTrue(Long ownerId);
    boolean existsByNameAndOwner_Id(String name, Long ownerId);

    long countByOwnerIdAndIsActiveTrue(Long ownerId);

    // 🐾 Microchip and species queries
    Optional<Pet> findByMicrochipId(String microchipId);
    List<Pet> findBySpecies(String species);

    

    boolean existsByMicrochipId(String microchipId);

    List<Pet> findByIsActiveTrue();

    Optional<Object> findByIdAndOwnerId(Long id, Long ownerId);

    List<Pet> findByBreed(String breed);

    boolean existsByNameAndOwnerId(String name, Long ownerId);

    List<Pet> findByOwnerIdAndIsActiveTrue(Long ownerId);

    List<Pet> findByOwnerId(Long ownerId);

    List<Pet> findByNameContainingIgnoreCaseAndOwnerId(String trim, Long ownerId);

    List<Pet> findByNameContainingIgnoreCaseAndOwnerIdAndIsActiveTrue(String trim, Long ownerId);

    long countByOwnerId(Long ownerId);
}

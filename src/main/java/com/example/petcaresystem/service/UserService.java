package com.example.petcaresystem.service;

import com.example.petcaresystem.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    List<User> getAllUsers();
    User getUserById(Long id);
    List<User> getUsersByRole(String role);
    User updateUser(Long id, User userDetails);
    void deleteUser(Long id);
    User authenticateUser(String username, String password);
    User authenticateUserByEmail(String email, String password);
    User authenticateUserFlexible(String usernameOrEmail, String password);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<User> getUserByUsernameOrEmail(String usernameOrEmail);
    List<User> getAllPetOwners();
    long countUsersByEmail(String email);
    boolean isDatabaseConnected();
}
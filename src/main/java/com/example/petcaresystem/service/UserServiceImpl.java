package com.example.petcaresystem.service;

import com.example.petcaresystem.model.User;
import com.example.petcaresystem.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        try {
            log.info("📝 Creating new user: {}", user.getUsername());

            // Enhanced validation
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                throw new RuntimeException("Username is required");
            }
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                throw new RuntimeException("Email is required");
            }
            if (user.getPassword() == null || user.getPassword().length() < 6) {
                throw new RuntimeException("Password must be at least 6 characters");
            }
            if (user.getRole() == null || user.getRole().trim().isEmpty()) {
                throw new RuntimeException("Role is required");
            }

            // Check if username already exists
            if (userRepository.existsByUsername(user.getUsername())) {
                log.warn("❌ Username already exists: {}", user.getUsername());
                throw new RuntimeException("Username '" + user.getUsername() + "' already exists");
            }

            // Check if email already exists
            if (userRepository.existsByEmail(user.getEmail())) {
                log.warn("❌ Email already exists: {}", user.getEmail());
                throw new RuntimeException("Email '" + user.getEmail() + "' already exists");
            }

            log.info("🔧 Saving user to database...");
            User savedUser = userRepository.save(user);
            log.info("✅ User created successfully with ID: {}", savedUser.getId());
            return savedUser;

        } catch (DataIntegrityViolationException e) {
            log.error("❌ Data integrity violation while creating user: {}", e.getMessage());
            throw new RuntimeException("Database constraint violation: Username or email already exists");
        } catch (Exception e) {
            log.error("❌ Error creating user: {}", e.getMessage());
            throw new RuntimeException("Error creating user: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        try {
            log.info("📋 Retrieving all users");
            List<User> users = userRepository.findAll();
            log.info("✅ Retrieved {} users", users.size());
            return users;
        } catch (Exception e) {
            log.error("❌ Error retrieving users: {}", e.getMessage());
            throw new RuntimeException("Error retrieving users: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        try {
            log.info("🔍 Retrieving user by ID: {}", id);
            Optional<User> user = userRepository.findById(id);
            if (user.isPresent()) {
                log.info("✅ User found with ID: {}", id);
                return user.get();
            } else {
                log.warn("⚠️ User not found with ID: {}", id);
                return null;
            }
        } catch (Exception e) {
            log.error("❌ Error retrieving user by id {}: {}", id, e.getMessage());
            throw new RuntimeException("Error retrieving user: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsersByRole(String role) {
        try {
            log.info("👥 Retrieving users by role: {}", role);
            List<User> users = userRepository.findByRole(role);
            log.info("✅ Retrieved {} users with role: {}", users.size(), role);
            return users;
        } catch (Exception e) {
            log.error("❌ Error retrieving users by role {}: {}", role, e.getMessage());
            throw new RuntimeException("Error retrieving users by role: " + e.getMessage());
        }
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        try {
            log.info("🔄 Updating user with ID: {}", id);
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                // Check if new username is taken by another user
                if (!user.getUsername().equals(userDetails.getUsername()) &&
                        userRepository.existsByUsername(userDetails.getUsername())) {
                    log.warn("❌ Username already taken: {}", userDetails.getUsername());
                    throw new RuntimeException("Username '" + userDetails.getUsername() + "' already taken");
                }

                // Check if new email is taken by another user
                if (!user.getEmail().equals(userDetails.getEmail()) &&
                        userRepository.existsByEmail(userDetails.getEmail())) {
                    log.warn("❌ Email already taken: {}", userDetails.getEmail());
                    throw new RuntimeException("Email '" + userDetails.getEmail() + "' already taken");
                }

                user.setUsername(userDetails.getUsername());
                user.setEmail(userDetails.getEmail());
                user.setPassword(userDetails.getPassword());
                user.setRole(userDetails.getRole());

                User updatedUser = userRepository.save(user);
                log.info("✅ User updated successfully: {}", updatedUser.getId());
                return updatedUser;
            }
            log.warn("⚠️ User not found with id: {}", id);
            throw new RuntimeException("User not found with id: " + id);
        } catch (Exception e) {
            log.error("❌ Error updating user with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error updating user: " + e.getMessage());
        }
    }

    @Override
    public void deleteUser(Long id) {
        try {
            log.info("🗑️ Deleting user with ID: {}", id);
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                log.info("✅ User deleted successfully: {}", id);
            } else {
                log.warn("⚠️ User not found with id: {}", id);
                throw new RuntimeException("User not found with id: " + id);
            }
        } catch (Exception e) {
            log.error("❌ Error deleting user with ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Error deleting user: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User authenticateUser(String username, String password) {
        try {
            log.info("🔐 Authenticating user with username: {}", username);
            User user = userRepository.findByUsernameAndPassword(username, password);
            if (user != null) {
                log.info("✅ User authenticated successfully with username: {}", username);
                log.info("✅ User details - ID: {}, Role: {}, Email: {}",
                        user.getId(), user.getRole(), user.getEmail());
            } else {
                log.warn("❌ Authentication failed for username: {}", username);
                User userByUsername = userRepository.findByUsername(username);
                if (userByUsername != null) {
                    log.warn("🔍 Username exists but password is incorrect");
                } else {
                    log.warn("🔍 Username not found in database");
                }
            }
            return user;
        } catch (Exception e) {
            log.error("❌ Error authenticating user {}: {}", username, e.getMessage());
            throw new RuntimeException("Error authenticating user: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User authenticateUserByEmail(String email, String password) {
        try {
            log.info("🔐 Authenticating user with email: {}", email);

            // First, check if email exists in database
            User userByEmail = userRepository.findByEmail(email);
            if (userByEmail != null) {
                log.info("🔍 Email found in database: {}", email);
                log.info("🔍 User details - Username: {}, Role: {}",
                        userByEmail.getUsername(), userByEmail.getRole());
            } else {
                log.warn("🔍 Email not found in database: {}", email);
            }

            // Now try authentication
            User user = userRepository.findByEmailAndPassword(email, password);
            if (user != null) {
                log.info("✅ User authenticated successfully with email: {}", email);
                log.info("✅ User details - ID: {}, Username: {}, Role: {}",
                        user.getId(), user.getUsername(), user.getRole());
            } else {
                log.warn("❌ Authentication failed for email: {}", email);
                if (userByEmail != null) {
                    log.warn("🔍 Email exists but password is incorrect");
                }
            }
            return user;
        } catch (Exception e) {
            log.error("❌ Error authenticating user with email {}: {}", email, e.getMessage());
            throw new RuntimeException("Error authenticating user: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User authenticateUserFlexible(String usernameOrEmail, String password) {
        try {
            log.info("🔐 Flexible authentication for: {}", usernameOrEmail);

            // Debug: Check if input exists as username or email
            Optional<User> userOptional = userRepository.findByUsernameOrEmail(usernameOrEmail);
            if (userOptional.isPresent()) {
                User foundUser = userOptional.get();
                log.info("🔍 User found - Username: {}, Email: {}, Role: {}",
                        foundUser.getUsername(), foundUser.getEmail(), foundUser.getRole());
            } else {
                log.warn("🔍 No user found with username/email: {}", usernameOrEmail);
            }

            // Try username first
            User user = userRepository.findByUsernameAndPassword(usernameOrEmail, password);
            if (user != null) {
                log.info("✅ User authenticated successfully with username: {}", usernameOrEmail);
                log.info("✅ User details - ID: {}, Email: {}, Role: {}",
                        user.getId(), user.getEmail(), user.getRole());
                return user;
            }

            // Try email if username failed
            user = userRepository.findByEmailAndPassword(usernameOrEmail, password);
            if (user != null) {
                log.info("✅ User authenticated successfully with email: {}", usernameOrEmail);
                log.info("✅ User details - ID: {}, Username: {}, Role: {}",
                        user.getId(), user.getUsername(), user.getRole());
                return user;
            }

            log.warn("❌ Flexible authentication failed for: {}", usernameOrEmail);
            log.warn("🔍 Both username and email authentication attempts failed");
            return null;
        } catch (Exception e) {
            log.error("❌ Error authenticating user {}: {}", usernameOrEmail, e.getMessage());
            throw new RuntimeException("Error authenticating user: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        try {
            boolean exists = userRepository.existsByUsername(username);
            log.info("🔍 Username existence check for '{}': {}", username, exists);
            return exists;
        } catch (Exception e) {
            log.error("❌ Error checking username existence {}: {}", username, e.getMessage());
            throw new RuntimeException("Error checking username existence: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        try {
            boolean exists = userRepository.existsByEmail(email);
            log.info("🔍 Email existence check for '{}': {}", email, exists);
            return exists;
        } catch (Exception e) {
            log.error("❌ Error checking email existence {}: {}", email, e.getMessage());
            throw new RuntimeException("Error checking email existence: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsernameOrEmail(String usernameOrEmail) {
        try {
            log.info("🔍 Retrieving user by username/email: {}", usernameOrEmail);
            Optional<User> user = userRepository.findByUsernameOrEmail(usernameOrEmail);
            if (user.isPresent()) {
                log.info("✅ User found: {}", user.get().getUsername());
            } else {
                log.warn("⚠️ User not found with username/email: {}", usernameOrEmail);
            }
            return user;
        } catch (Exception e) {
            log.error("❌ Error retrieving user by username/email {}: {}", usernameOrEmail, e.getMessage());
            throw new RuntimeException("Error retrieving user: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsersByEmail(String email) {
        try {
            long count = userRepository.countByEmail(email);
            log.info("🔍 Count of users with email '{}': {}", email, count);
            return count;
        } catch (Exception e) {
            log.error("❌ Error counting users by email {}: {}", email, e.getMessage());
            return 0;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllPetOwners() {
        try {
            log.info("🐾 Retrieving all PET_OWNER or OWNER users");
            return userRepository.findAllPetOwners();
        } catch (Exception e) {
            log.error("❌ Error retrieving pet owners: {}", e.getMessage());
            throw new RuntimeException("Error retrieving pet owners: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isDatabaseConnected() {
        try {
            long userCount = userRepository.count();
            log.info("🔍 Database connection check - Total users: {}", userCount);
            return true;
        } catch (Exception e) {
            log.error("❌ Database connection failed: {}", e.getMessage());
            return false;
        }
    }
}
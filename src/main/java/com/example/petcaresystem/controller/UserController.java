package com.example.petcaresystem.controller;

import com.example.petcaresystem.model.User;
import com.example.petcaresystem.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ---------------- HEALTH CHECK ----------------
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "User Service");
        health.put("database", userService.isDatabaseConnected() ? "CONNECTED" : "DISCONNECTED");
        return ResponseEntity.ok(health);
    }

    // ---------------- CREATE USER ----------------
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody User user) {
        try {
            // Enhanced validation
            if (user.getUsername() == null || user.getUsername().trim().isEmpty())
                return ResponseEntity.badRequest().body("Username is required");
            if (user.getEmail() == null || user.getEmail().trim().isEmpty())
                return ResponseEntity.badRequest().body("Email is required");
            if (user.getPassword() == null || user.getPassword().length() < 6)
                return ResponseEntity.badRequest().body("Password must be at least 6 characters");
            if (user.getRole() == null || user.getRole().trim().isEmpty())
                return ResponseEntity.badRequest().body("Role is required");

            // Email format validation
            if (!isValidEmail(user.getEmail())) {
                return ResponseEntity.badRequest().body("Invalid email format");
            }

            // Check duplicates
            if (userService.existsByUsername(user.getUsername()))
                return ResponseEntity.badRequest().body("Username already exists");
            if (userService.existsByEmail(user.getEmail()))
                return ResponseEntity.badRequest().body("Email already exists");

            User created = userService.createUser(user);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating user: " + e.getMessage());
        }
    }

    // ---------------- GET ALL USERS ----------------
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving users: " + e.getMessage());
        }
    }

    // ---------------- GET USER BY ID ----------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            if (user == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving user: " + e.getMessage());
        }
    }

    // ---------------- GET USERS BY ROLE ----------------
    @GetMapping("/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            List<User> users = userService.getUsersByRole(role);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving users by role: " + e.getMessage());
        }
    }

    // ---------------- GET ALL OWNERS ----------------
    @GetMapping("/owners")
    public ResponseEntity<?> getAllPetOwners() {
        try {
            List<User> owners = userService.getAllPetOwners();
            if (owners.isEmpty())
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No pet owners found");
            return ResponseEntity.ok(owners);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving pet owners: " + e.getMessage());
        }
    }

    // ---------------- LOGIN ----------------
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty())
                return ResponseEntity.badRequest().body("Email is required");
            if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty())
                return ResponseEntity.badRequest().body("Password is required");

            User user = userService.authenticateUserByEmail(
                    loginRequest.getEmail(), loginRequest.getPassword());

            if (user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("user", user);
                response.put("message", "Login successful");
                response.put("timestamp", LocalDateTime.now());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid email or password");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Login error: " + e.getMessage());
        }
    }

    // ---------------- FLEXIBLE LOGIN (USERNAME or EMAIL) ----------------
    @PostMapping("/login-flexible")
    public ResponseEntity<?> loginFlexible(@RequestBody LoginRequest loginRequest) {
        try {
            if (loginRequest.getLogin() == null || loginRequest.getLogin().isEmpty())
                return ResponseEntity.badRequest().body("Email or username required");
            if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty())
                return ResponseEntity.badRequest().body("Password required");

            User user = userService.authenticateUserFlexible(
                    loginRequest.getLogin(), loginRequest.getPassword());

            if (user != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("user", user);
                response.put("message", "Login successful");
                response.put("timestamp", LocalDateTime.now());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid credentials");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Login error: " + e.getMessage());
        }
    }

    // ---------------- ADMIN ENDPOINTS ----------------
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllUsersForAdmin() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving users: " + e.getMessage());
        }
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            response.put("userId", userId.toString());
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting user: " + e.getMessage());
        }
    }

    // ---------------- TEST ENDPOINTS ----------------
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("✅ User API is working!");
    }

    @GetMapping("/test-create")
    public ResponseEntity<?> testCreateUser() {
        try {
            User test = new User("demoUser", "demo@example.com", "password123", "PET_OWNER");
            User saved = userService.createUser(test);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Test user creation failed: " + e.getMessage());
        }
    }

    // ---------------- HELPER METHODS ----------------
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    // ---------------- INNER CLASS ----------------
    public static class LoginRequest {
        private String email;
        private String password;
        private String login;

        public LoginRequest() {}

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getLogin() { return login; }
        public void setLogin(String login) { this.login = login; }

        @Override
        public String toString() {
            return "LoginRequest{" +
                    "email='" + email + '\'' +
                    ", login='" + login + '\'' +
                    '}';
        }
    }
}
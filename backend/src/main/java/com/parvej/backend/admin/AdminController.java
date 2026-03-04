package com.parvej.backend.admin;

import com.parvej.backend.user.Users;
import com.parvej.backend.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserService userService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<Users> users = userService.getAllUsers();
            Map<String, Object> response = new HashMap<>();
            response.put("users", users);
            response.put("count", users.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch users: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody Users user, Authentication authentication) {
        try {
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Username is required");
                return ResponseEntity.badRequest().body(error);
            }
            if (user.getPassword() == null || user.getPassword().length() < 6) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Password must be at least 6 characters");
                return ResponseEntity.badRequest().body(error);
            }
            user.setPassword(encoder.encode(user.getPassword()));
            Users savedUser = userService.register(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User created successfully");
            response.put("user", savedUser);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create user: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody Users userDetails) {
        try {
            Users user = userService.getUserById(id);
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.notFound().build();
            }

            if (userDetails.getUsername() != null && !userDetails.getUsername().trim().isEmpty()) {
                // Check if username is being changed and if new username already exists
                if (!user.getUsername().equals(userDetails.getUsername())) {
                    Users existingUser = userService.getUserByUsername(userDetails.getUsername());
                    if (existingUser != null) {
                        Map<String, String> error = new HashMap<>();
                        error.put("error", "Username already exists");
                        return ResponseEntity.badRequest().body(error);
                    }
                }
                user.setUsername(userDetails.getUsername());
            }

            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                if (userDetails.getPassword().length() < 6) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Password must be at least 6 characters");
                    return ResponseEntity.badRequest().body(error);
                }
                user.setPassword(encoder.encode(userDetails.getPassword()));
            }

            Users updatedUser = userService.updateUser(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User updated successfully");
            response.put("user", updatedUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update user: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id, Authentication authentication) {
        try {
            Users user = userService.getUserById(id);
            if (user == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.notFound().build();
            }

            // Prevent admin from deleting themselves
            String currentUsername = authentication.getName();
            if (user.getUsername().equals(currentUsername)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "You cannot delete your own account");
                return ResponseEntity.badRequest().body(error);
            }

            userService.deleteUser(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete user: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        try {
            Users user = userService.getUserById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch user: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/csrf-token")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }
}



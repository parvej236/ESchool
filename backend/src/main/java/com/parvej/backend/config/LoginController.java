package com.parvej.backend.config;

import com.parvej.backend.config.EmailService;
import com.parvej.backend.user.UserService;
import com.parvej.backend.user.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    private UserService service;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users user) {
        try {
            if(user.getEmail() == null || user.getEmail().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Email is required");
                return ResponseEntity.badRequest().body(error);
            }
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
            Users savedUser = service.register(user);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("user", savedUser);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users user) {
        try {
            String token = service.verify(user);
            if (token != null && !token.equals("fail")) {
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("message", "Login successful");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid credentials");
                return ResponseEntity.status(401).body(error);
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Login failed: " + e.getMessage());
            return ResponseEntity.status(401).body(error);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            Users user = service.findByEmail(email);
            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "User with this email does not exist"));
            }

            // Generate token
            String token = java.util.UUID.randomUUID().toString();
            long expiryTime = System.currentTimeMillis() + (1000 * 60 * 15); // 15 minutes

            user.setResetToken(token);
            user.setResetTokenExpiry(expiryTime);
            user = service.save(user);

            // Reset link (frontend URL)
            String resetLink = "http://localhost:5173/reset-password?token=" + token;
            emailService.sendResetEmail(user.getEmail(), resetLink);
            return ResponseEntity.ok(Map.of("message", "Password reset link sent to email"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to process request"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            String newPassword = request.get("password");

            if (token == null || newPassword == null || newPassword.length() < 6) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid request"));
            }

            Users user = service.findByResetToken(token);
            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid or expired token"));
            }

            if (user.getResetTokenExpiry() < System.currentTimeMillis()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Token has expired"));
            }

            user.setPassword(encoder.encode(newPassword));
            user.setResetToken(null);
            user.setResetTokenExpiry(null);

            service.save(user);

            return ResponseEntity.ok(Map.of("message", "Password reset successful"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Password reset failed"));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request,
                                            Authentication authentication) {
        try {
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");

            if (currentPassword == null || newPassword == null || newPassword.length() < 6) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid password data"));
            }

            // Get currently authenticated user
            String username = authentication.getName();
            Users user = service.getUserByUsername(username);
            if (user == null) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "User not found"));
            }

            // Verify current password
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
            if (!encoder.matches(currentPassword, user.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Current password is incorrect"));
            }

            // Update password
            user.setPassword(encoder.encode(newPassword));
            service.save(user);

            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to change password"));
        }
    }
}

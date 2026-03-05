package com.parvej.backend.config;

import com.parvej.backend.user.UserService;
import com.parvej.backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LoginController {

    private final UserService service;
    private final EmailService emailService;
    private final JWTService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            if (user.getEmail() == null || user.getEmail().isEmpty())
                return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));

            if (user.getUsername() == null || user.getUsername().trim().isEmpty())
                return ResponseEntity.badRequest().body(Map.of("error", "Username is required"));

            if (user.getPassword() == null || user.getPassword().length() < 6)
                return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 6 characters"));

            user.setPassword(encoder.encode(user.getPassword()));
            User savedUser = service.register(user);

            return ResponseEntity.ok(Map.of(
                    "message", "User registered successfully",
                    "user",    savedUser
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }

    // POST /api/auth/login
    // Returns both access token (10 min) and refresh token (7 days)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            String accessToken = service.verify(user);
            if (accessToken != null && !accessToken.equals("fail")) {
                String refreshToken = jwtService.generateRefreshToken(user.getUsername());
                return ResponseEntity.ok(Map.of(
                        "token",        accessToken,
                        "refreshToken", refreshToken,
                        "message",      "Login successful"
                ));
            }
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }

    // POST /api/auth/refresh
    // Body:    { "refreshToken": "..." }
    // Returns: { "token": "new_access_token" }
    // Public — no access token needed, refresh token IS the credential
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");

            if (refreshToken == null || refreshToken.isBlank())
                return ResponseEntity.status(401)
                        .body(Map.of("error", "Refresh token is required"));

            if (!jwtService.isRefreshToken(refreshToken))
                return ResponseEntity.status(401)
                        .body(Map.of("error", "Invalid token type"));

            if (jwtService.isTokenExpired(refreshToken))
                return ResponseEntity.status(401).body(Map.of(
                        "error",   "SESSION_EXPIRED",
                        "message", "Session expired. Please log in again."
                ));

            String username      = jwtService.extractUsername(refreshToken);
            String newAccessToken = jwtService.generateToken(username);

            return ResponseEntity.ok(Map.of(
                    "token",   newAccessToken,
                    "message", "Token refreshed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Token refresh failed: " + e.getMessage()));
        }
    }

    // POST /api/auth/forgot-password
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            User user = service.findByEmail(email);

            if (user == null)
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "User with this email does not exist"));

            String token      = java.util.UUID.randomUUID().toString();
            long   expiryTime = System.currentTimeMillis() + (1000 * 60 * 15); // 15 min

            user.setResetToken(token);
            user.setResetTokenExpiry(expiryTime);
            service.save(user);

            String resetLink = "http://localhost:5173/reset-password?token=" + token;
            emailService.sendResetEmail(user.getEmail(), resetLink);

            return ResponseEntity.ok(Map.of("message", "Password reset link sent to email"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to process request"));
        }
    }

    // POST /api/auth/reset-password
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String token       = request.get("token");
            String newPassword = request.get("password");

            if (token == null || newPassword == null || newPassword.length() < 6)
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid request"));

            User user = service.findByResetToken(token);
            if (user == null)
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid or expired token"));

            if (user.getResetTokenExpiry() < System.currentTimeMillis())
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Token has expired"));

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

    // POST /api/auth/change-password
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> request,
            Authentication authentication
    ) {
        try {
            String currentPassword = request.get("currentPassword");
            String newPassword     = request.get("newPassword");

            if (currentPassword == null || newPassword == null || newPassword.length() < 6)
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid password data"));

            String username = authentication.getName();
            User   user     = service.getUserByUsername(username);

            if (user == null)
                return ResponseEntity.status(404)
                        .body(Map.of("error", "User not found"));

            if (!encoder.matches(currentPassword, user.getPassword()))
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Current password is incorrect"));

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
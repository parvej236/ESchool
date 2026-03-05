package com.parvej.backend.dashboard;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @GetMapping("/data")
    public ResponseEntity<?> getDashboardData(Authentication authentication) {
        try {
            if (authentication == null) {
                return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
            }
            String username = authentication.getName();
            Map<String, Object> data = new HashMap<>();
            data.put("message", "Welcome to the Dashboard, " + username + "!");
            data.put("username", username);
            data.put("status", "authenticated");
            data.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch dashboard data: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}


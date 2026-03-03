package com.parvej.backend.admin;

import com.parvej.backend.classInfo.ClassInfoRepo;
import com.parvej.backend.role.RoleRepo;
import com.parvej.backend.user.UserRepo;
import com.parvej.backend.user.UserService;
import com.parvej.backend.user.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ClassInfoRepo classInfoRepo;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepo roleRepo;

    @GetMapping("/data")
    public ResponseEntity<?> getDashboardData(Authentication authentication) {
        try {
            String username = authentication != null ? authentication.getName() : "User";
            
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

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        try {
            long totalUsers = userRepo.count();
            long totalClasses = classInfoRepo.count();
            long totalRoles = roleRepo.count();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", totalUsers);
            stats.put("totalOrders", 0); // Placeholder for future orders feature
            stats.put("totalClasses", totalClasses);
            stats.put("totalRoles", totalRoles);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch statistics: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {

            if (authentication == null) {
                return ResponseEntity.status(401)
                        .body(Map.of("error", "Unauthorized"));
            }

            String username = authentication.getName();
            Users user = userService.getUserByUsername(username);

            if (user == null) {
                return ResponseEntity.status(404)
                        .body(Map.of("error", "User not found"));
            }

            Map<String, Object> profile = new HashMap<>();
            profile.put("username", user.getUsername());
            profile.put("email", user.getEmail());
            profile.put("role", user.getRole());


//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            profile.put("enabled", userDetails.isEnabled());

            return ResponseEntity.ok(profile);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Failed to fetch profile: " + e.getMessage()));
        }
    }
}


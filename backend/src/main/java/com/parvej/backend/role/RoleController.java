package com.parvej.backend.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/roles")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    @Autowired
    private RoleRepo roleRepo;

    // Get all roles
    @GetMapping
    public ResponseEntity<?> getAllRoles() {
        try {
            List<Role> roles = roleRepo.findAll();
            Map<String, Object> response = new HashMap<>();
            response.put("roles", roles);
            response.put("count", roles.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch roles: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Get active roles only
    @GetMapping("/active")
    public ResponseEntity<?> getActiveRoles() {
        try {
            List<Role> roles = roleRepo.findByActiveTrue();
            Map<String, Object> response = new HashMap<>();
            response.put("roles", roles);
            response.put("count", roles.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch active roles: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Get role by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable Integer id) {
        try {
            Role role = roleRepo.findById(id).orElse(null);
            if (role == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Role not found");
                return ResponseEntity.status(404).body(error);
            }
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch role: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Create new role
    @PostMapping
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        try {
            // Validate required fields
            if (role.getName() == null || role.getName().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Role name is required");
                return ResponseEntity.badRequest().body(error);
            }

            // Check if role name already exists
            String roleName = role.getName().trim().toUpperCase();
            if (roleRepo.existsByName(roleName)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Role with this name already exists");
                return ResponseEntity.badRequest().body(error);
            }

            // Prevent creating ADMIN role (should be system-managed)
            if (roleName.equals("ADMIN")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "ADMIN role is system-managed and cannot be created");
                return ResponseEntity.badRequest().body(error);
            }

            role.setName(roleName);
            if (role.getActive() == null) {
                role.setActive(true);
            }
            if (role.getDescription() == null) {
                role.setDescription("");
            }

            Role savedRole = roleRepo.save(role);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Role created successfully");
            response.put("role", savedRole);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create role: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(error);
        }
    }

    // Update role
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRole(@PathVariable Integer id, @RequestBody Role roleDetails) {
        try {
            Role role = roleRepo.findById(id).orElse(null);
            if (role == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Role not found");
                return ResponseEntity.status(404).body(error);
            }

            // Prevent modifying ADMIN role
            if (role.getName().equals("ADMIN")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "ADMIN role is system-managed and cannot be modified");
                return ResponseEntity.badRequest().body(error);
            }

            // Validate and update name if provided
            if (roleDetails.getName() != null && !roleDetails.getName().trim().isEmpty()) {
                String newName = roleDetails.getName().trim().toUpperCase();
                
                // Check if new name conflicts with existing role (except current role)
                if (!newName.equals(role.getName()) && roleRepo.existsByName(newName)) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Role with this name already exists");
                    return ResponseEntity.badRequest().body(error);
                }
                
                // Prevent renaming to ADMIN
                if (newName.equals("ADMIN")) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Cannot rename role to ADMIN");
                    return ResponseEntity.badRequest().body(error);
                }
                
                role.setName(newName);
            }

            // Update description
            if (roleDetails.getDescription() != null) {
                role.setDescription(roleDetails.getDescription());
            }

            // Update active status
            if (roleDetails.getActive() != null) {
                role.setActive(roleDetails.getActive());
            }

            Role updatedRole = roleRepo.save(role);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Role updated successfully");
            response.put("role", updatedRole);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update role: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(error);
        }
    }

    // Delete role
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Integer id) {
        try {
            Role role = roleRepo.findById(id).orElse(null);
            if (role == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Role not found");
                return ResponseEntity.status(404).body(error);
            }

            // Prevent deleting ADMIN and USER roles (system-managed)
            if (role.getName().equals("ADMIN") || role.getName().equals("USER")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", role.getName() + " role is system-managed and cannot be deleted");
                return ResponseEntity.badRequest().body(error);
            }

            roleRepo.deleteById(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Role deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete role: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(error);
        }
    }
}



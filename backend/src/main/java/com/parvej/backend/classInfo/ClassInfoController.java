package com.parvej.backend.classInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/classes")
@CrossOrigin(origins = "*")
public class ClassInfoController {

    @Autowired
    private ClassInfoRepo classInfoRepo;

    // Public endpoint - get all active classes
    @GetMapping("/active")
    public ResponseEntity<?> getActiveClasses() {
        try {
            List<ClassInfo> classes = classInfoRepo.findByActiveTrue();
            Map<String, Object> response = new HashMap<>();
            response.put("classes", classes);
            response.put("count", classes.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch classes: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Get all classes (active and inactive)
    @GetMapping("/all")
    public ResponseEntity<?> getAllClassesAdmin() {
        try {
            List<ClassInfo> classes = classInfoRepo.findAll();
            Map<String, Object> response = new HashMap<>();
            response.put("classes", classes);
            response.put("count", classes.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch classes: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Create class
    @PostMapping
    public ResponseEntity<?> createClass(@RequestBody ClassInfo classInfo) {
        try {
            // Validate required fields
            if (classInfo.getClassName() == null || classInfo.getClassName().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Class name is required");
                return ResponseEntity.badRequest().body(error);
            }
            if (classInfo.getInstructor() == null || classInfo.getInstructor().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Instructor is required");
                return ResponseEntity.badRequest().body(error);
            }
            if (classInfo.getSchedule() == null || classInfo.getSchedule().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Schedule is required");
                return ResponseEntity.badRequest().body(error);
            }
            if (classInfo.getCapacity() == null || classInfo.getCapacity() <= 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Capacity must be greater than 0");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Set defaults
            if (classInfo.getActive() == null) {
                classInfo.setActive(true);
            }
            if (classInfo.getEnrolled() == null) {
                classInfo.setEnrolled(0);
            }
            
            // Ensure enrolled doesn't exceed capacity
            if (classInfo.getEnrolled() > classInfo.getCapacity()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Enrolled count cannot exceed capacity");
                return ResponseEntity.badRequest().body(error);
            }

            // Set createdAt if not already set (will be set by @PrePersist, but this ensures it's set)
            if (classInfo.getCreatedAt() == null) {
                classInfo.setCreatedAt(java.time.LocalDateTime.now());
            }

            ClassInfo savedClass = classInfoRepo.save(classInfo);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Class created successfully");
            response.put("class", savedClass);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create class: " + e.getMessage());
            e.printStackTrace(); // Log the full exception for debugging
            return ResponseEntity.status(500).body(error);
        }
    }

    // Update class
    @PutMapping("/{id}")
    public ResponseEntity<?> updateClass(@PathVariable Integer id, @RequestBody ClassInfo classDetails) {
        try {
            ClassInfo classInfo = classInfoRepo.findById(id).orElse(null);
            if (classInfo == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Class not found");
                return ResponseEntity.status(404).body(error);
            }

            // Validate required fields
            if (classDetails.getClassName() == null || classDetails.getClassName().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Class name is required");
                return ResponseEntity.badRequest().body(error);
            }
            if (classDetails.getInstructor() == null || classDetails.getInstructor().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Instructor is required");
                return ResponseEntity.badRequest().body(error);
            }
            if (classDetails.getSchedule() == null || classDetails.getSchedule().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Schedule is required");
                return ResponseEntity.badRequest().body(error);
            }
            if (classDetails.getCapacity() == null || classDetails.getCapacity() <= 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Capacity must be greater than 0");
                return ResponseEntity.badRequest().body(error);
            }
            if (classDetails.getEnrolled() == null || classDetails.getEnrolled() < 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Enrolled count cannot be negative");
                return ResponseEntity.badRequest().body(error);
            }
            if (classDetails.getEnrolled() > classDetails.getCapacity()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Enrolled count cannot exceed capacity");
                return ResponseEntity.badRequest().body(error);
            }

            // Update fields
            classInfo.setClassName(classDetails.getClassName());
            classInfo.setDescription(classDetails.getDescription());
            classInfo.setInstructor(classDetails.getInstructor());
            classInfo.setSchedule(classDetails.getSchedule());
            classInfo.setCapacity(classDetails.getCapacity());
            classInfo.setEnrolled(classDetails.getEnrolled());
            if (classDetails.getActive() != null) {
                classInfo.setActive(classDetails.getActive());
            }

            ClassInfo updatedClass = classInfoRepo.save(classInfo);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Class updated successfully");
            response.put("class", updatedClass);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update class: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Delete class
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClass(@PathVariable Integer id) {
        try {
            if (!classInfoRepo.existsById(id)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Class not found");
                return ResponseEntity.status(404).body(error);
            }

            classInfoRepo.deleteById(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Class deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete class: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Get class by ID (must be after /active and /all to avoid path conflicts)
    @GetMapping("/details/{id}")
    public ResponseEntity<?> getClassById(@PathVariable Integer id) {
        try {
            ClassInfo classInfo = classInfoRepo.findById(id).orElse(null);
            if (classInfo == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Class not found");
                return ResponseEntity.status(404).body(error);
            }
            return ResponseEntity.ok(classInfo);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch class: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

}


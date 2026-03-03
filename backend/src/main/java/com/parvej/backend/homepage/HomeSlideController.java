package com.parvej.backend.homepage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/home-slides")
@CrossOrigin(origins = "*")
public class HomeSlideController {

    @Autowired
    private HomeSlideRepo homeSlideRepo;

    // Public endpoint - get all active slides for home page
    @GetMapping("/active")
    public ResponseEntity<?> getActiveSlides() {
        try {
            List<HomeSlide> slides = homeSlideRepo.findByActiveTrueOrderByDisplayOrderAsc();
            Map<String, Object> response = new HashMap<>();
            response.put("slides", slides);
            response.put("count", slides.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch slides: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Admin only - get all slides
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllSlides() {
        try {
            List<HomeSlide> slides = homeSlideRepo.findAllByOrderByDisplayOrderAsc();
            Map<String, Object> response = new HashMap<>();
            response.put("slides", slides);
            response.put("count", slides.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch slides: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    // Admin only - create slide
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSlide(@RequestBody HomeSlide slide) {
        try {
            // Validate required fields
            if (slide.getTitle() == null || slide.getTitle().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Title is required");
                return ResponseEntity.badRequest().body(error);
            }
            if (slide.getImageUrl() == null || slide.getImageUrl().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Image URL is required");
                return ResponseEntity.badRequest().body(error);
            }

            // Set defaults
            if (slide.getDisplayOrder() == null) {
                slide.setDisplayOrder(0);
            }
            if (slide.getActive() == null) {
                slide.setActive(true);
            }

            HomeSlide savedSlide = homeSlideRepo.save(slide);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Slide created successfully");
            response.put("slide", savedSlide);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create slide: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(error);
        }
    }

    // Admin only - update slide
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSlide(@PathVariable Integer id, @RequestBody HomeSlide slideDetails) {
        try {
            HomeSlide slide = homeSlideRepo.findById(id).orElse(null);
            if (slide == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Slide not found");
                return ResponseEntity.status(404).body(error);
            }

            // Validate required fields
            if (slideDetails.getTitle() != null && !slideDetails.getTitle().trim().isEmpty()) {
                slide.setTitle(slideDetails.getTitle());
            } else if (slideDetails.getTitle() != null && slideDetails.getTitle().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Title cannot be empty");
                return ResponseEntity.badRequest().body(error);
            }

            if (slideDetails.getDescription() != null) {
                slide.setDescription(slideDetails.getDescription());
            }

            if (slideDetails.getImageUrl() != null && !slideDetails.getImageUrl().trim().isEmpty()) {
                slide.setImageUrl(slideDetails.getImageUrl());
            }

            if (slideDetails.getLinkUrl() != null) {
                slide.setLinkUrl(slideDetails.getLinkUrl());
            }

            if (slideDetails.getDisplayOrder() != null) {
                slide.setDisplayOrder(slideDetails.getDisplayOrder());
            }

            if (slideDetails.getActive() != null) {
                slide.setActive(slideDetails.getActive());
            }

            HomeSlide updatedSlide = homeSlideRepo.save(slide);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Slide updated successfully");
            response.put("slide", updatedSlide);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update slide: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(error);
        }
    }

    // Admin only - delete slide
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSlide(@PathVariable Integer id) {
        try {
            if (!homeSlideRepo.existsById(id)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Slide not found");
                return ResponseEntity.status(404).body(error);
            }

            homeSlideRepo.deleteById(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Slide deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete slide: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(error);
        }
    }

    // Admin only - get slide by ID
    @GetMapping("/details/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getSlideById(@PathVariable Integer id) {
        try {
            HomeSlide slide = homeSlideRepo.findById(id).orElse(null);
            if (slide == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Slide not found");
                return ResponseEntity.status(404).body(error);
            }
            return ResponseEntity.ok(slide);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch slide: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}


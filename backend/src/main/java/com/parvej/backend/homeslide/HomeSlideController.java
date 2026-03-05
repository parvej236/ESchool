package com.parvej.backend.homeslide;

import com.parvej.backend.common.ApiResponse;
import com.parvej.backend.config.FileStorageConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/home-slides")
@RequiredArgsConstructor
public class HomeSlideController {

    private final HomeSlideService  service;
    private final FileStorageConfig fileStorageConfig;

    // ── Public endpoints ────────────────────────────────

    // GET /api/home-slides/active
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<HomeSlide>>> getActiveSlides() {
        try {
            return ResponseEntity.ok(
                    ApiResponse.success("Active slides fetched", service.getActiveSlides())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── Protected endpoints (JWT required) ─────────────

    // GET /api/home-slides/all
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<HomeSlide>>> getAll() {
        try {
            return ResponseEntity.ok(
                    ApiResponse.success("Slides fetched", service.getAll())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // GET /api/home-slides/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HomeSlide>> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(
                    ApiResponse.success("Slide fetched", service.getById(id))
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // POST /api/home-slides/create
    // Content-Type: multipart/form-data
    // Fields: title, description, linkUrl, displayOrder, active, image (file)
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<HomeSlide>> create(
            @RequestParam("title")                        String         title,
            @RequestParam(value = "description",          required = false) String description,
            @RequestParam(value = "linkUrl",              required = false) String linkUrl,
            @RequestParam(value = "displayOrder",         required = false, defaultValue = "0") Integer displayOrder,
            @RequestParam(value = "active",               required = false, defaultValue = "true") Boolean active,
            @RequestParam("image")                        MultipartFile  image,
            @RequestParam(value = "titleBn",        required = false) String titleBn,
            @RequestParam(value = "descriptionBn",  required = false) String descriptionBn
    ) {
        try {
            String imageUrl = saveImage(image);

            HomeSlideDTO dto = new HomeSlideDTO();
            dto.setTitle(title);
            dto.setDescription(description);
            dto.setLinkUrl(linkUrl);
            dto.setDisplayOrder(displayOrder);
            dto.setActive(active);
            dto.setTitleBn(titleBn);
            dto.setDescriptionBn(descriptionBn);

            HomeSlide saved = service.create(dto, imageUrl);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Slide created successfully", saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // PUT /api/home-slides/update/{id}
    // Content-Type: multipart/form-data
    // image is optional on update — leave blank to keep existing
    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<HomeSlide>> update(
            @PathVariable                                 Long           id,
            @RequestParam("title")                        String         title,
            @RequestParam(value = "description",          required = false) String description,
            @RequestParam(value = "linkUrl",              required = false) String linkUrl,
            @RequestParam(value = "displayOrder",         required = false) Integer displayOrder,
            @RequestParam(value = "active",               required = false) Boolean active,
            @RequestParam(value = "image",                required = false) MultipartFile image,
            @RequestParam(value = "titleBn",        required = false) String titleBn,
            @RequestParam(value = "descriptionBn",  required = false) String descriptionBn
    ) {
        try {
            // Save new image only if one was provided
            String imageUrl = (image != null && !image.isEmpty()) ? saveImage(image) : null;

            HomeSlideDTO dto = new HomeSlideDTO();
            dto.setTitle(title);
            dto.setDescription(description);
            dto.setLinkUrl(linkUrl);
            dto.setDisplayOrder(displayOrder);
            dto.setActive(active);
            dto.setTitleBn(titleBn);
            dto.setDescriptionBn(descriptionBn);

            HomeSlide updated = service.update(id, dto, imageUrl);
            return ResponseEntity.ok(ApiResponse.success("Slide updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // DELETE /api/home-slides/delete/{id}
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            // Optionally delete the image file from disk too
            HomeSlide slide = service.getById(id);
            deleteImageFile(slide.getImageUrl());

            service.delete(id);
            return ResponseEntity.ok(ApiResponse.success("Slide deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── File helpers ────────────────────────────────────

    /**
     * Saves the uploaded image to uploads/slides/<uuid>.<ext>
     * Returns the relative URL path: /uploads/slides/<uuid>.<ext>
     * This path is stored in the database as imageUrl
     */
    private String saveImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty())
            throw new RuntimeException("Image file is required");

        String originalName = file.getOriginalFilename();
        String extension    = "";

        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }

        // Validate extension
        if (!List.of(".jpg", ".jpeg", ".png", ".webp", ".gif").contains(extension))
            throw new RuntimeException("Invalid image type. Allowed: jpg, jpeg, png, webp, gif");

        // Generate unique filename to prevent collisions
        String filename = UUID.randomUUID() + extension;
        Path   filePath = Paths.get(fileStorageConfig.uploadDir, "slides", filename);

        Files.createDirectories(filePath.getParent());
        Files.write(filePath, file.getBytes());

        // Return the public URL path stored in DB
        // Frontend accesses it as: http://localhost:8080/uploads/slides/<filename>
        return "/uploads/slides/" + filename;
    }

    /** Deletes the image file from disk when a slide is deleted */
    private void deleteImageFile(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;
        try {
            // imageUrl is like "/uploads/slides/abc.jpg"
            // strip the leading "/" to get the relative path
            Path filePath = Paths.get(fileStorageConfig.uploadDir,
                    imageUrl.replaceFirst("^/uploads/", ""));
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log but don't fail the delete operation
            System.err.println("Could not delete image file: " + imageUrl);
        }
    }
}
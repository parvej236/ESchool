package com.parvej.backend.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:/var/www/parvej-backend/uploads}")   // reads from application.properties
    public String uploadDir;

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(Paths.get(uploadDir + "/slides"));
        // Add more subdirs as you add more upload types:
        // Files.createDirectories(Paths.get(uploadDir + "/avatars"));
        // Files.createDirectories(Paths.get(uploadDir + "/classes"));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Maps GET /uploads/** → /var/www/parvej-backend/uploads/
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }

    // ```
    //
    //        ---
    //
    //        ## How it all connects
    //```
    //    User uploads image
    //        ↓
    //    Spring saves file to:
    //            /var/www/parvej-backend/uploads/slides/a3f9c1d2-uuid.jpg
    //        ↓
    //    DB stores path:
    //    imageUrl = "/uploads/slides/a3f9c1d2-uuid.jpg"
    //            ↓
    //    Frontend loads image:
    //    http://localhost:8080/uploads/slides/a3f9c1d2-uuid.jpg
    //            ↓
    //    ResourceHandler serves the file from disk
    //```
    //
    //        ---
    //
    //        ## Folder structure on disk
    //```
    //        /var/www/parvej-backend/
    //            └── uploads/
    //            ├── slides/
    //            │   ├── a3f9c1d2-...jpg
    //    │   └── b7e2f4a1-...png
    //    ├── avatars/        ← add later
    //    └── classes/        ← add later
}

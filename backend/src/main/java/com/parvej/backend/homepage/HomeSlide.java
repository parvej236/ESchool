package com.parvej.backend.homepage;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "home_slides")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeSlide {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "home_slides_seq")
    @SequenceGenerator(name = "home_slides_seq", sequenceName = "home_slides_id_seq", allocationSize = 1)
    private Integer id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private String imageUrl;
    
    @Column
    private String linkUrl;
    
    @Column(nullable = false)
    private Integer displayOrder = 0;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}


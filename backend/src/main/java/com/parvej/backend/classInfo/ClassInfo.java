package com.parvej.backend.classInfo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "class_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "class_info_seq")
    @SequenceGenerator(name = "class_info_seq", sequenceName = "class_info_id_seq", allocationSize = 1)
    private Integer id;
    
    @Column(nullable = false)
    private String className;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private String instructor;
    
    @Column(nullable = false)
    private String schedule; // e.g., "Monday, Wednesday, Friday 10:00 AM - 11:30 AM"
    
    @Column(nullable = false)
    private Integer capacity;
    
    @Column(nullable = false)
    private Integer enrolled = 0;
    
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


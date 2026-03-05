package com.parvej.backend.homeslide;

import com.parvej.backend.common.AuditData;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "home_slides")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HomeSlide extends AuditData {

    // ── English content ────────────────────────────────
    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    // ── Bangla content ─────────────────────────────────
    @Column(name = "title_bn")
    private String titleBn;

    @Column(name = "description_bn", length = 1000)
    private String descriptionBn;

    // ── Common fields ──────────────────────────────────
    @Column(nullable = false)
    private String imageUrl;

    @Column
    private String linkUrl;

    @Column(nullable = false)
    private Integer displayOrder = 0;

    @Column(nullable = false)
    private Boolean active = true;
}
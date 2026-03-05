package com.parvej.backend.user;

import com.parvej.backend.common.AuditData;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User extends AuditData {

    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;

    private String resetToken;
    private Long resetTokenExpiry;
}

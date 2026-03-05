package com.parvej.backend.dto;

import com.parvej.backend.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private Date createdAt;
    private Date updatedAt;

    // Static factory — converts User entity → safe response
    public static UserResponseDTO from(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}

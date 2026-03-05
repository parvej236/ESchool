package com.parvej.backend.user;

import com.parvej.backend.dto.UserRequestDTO;
import com.parvej.backend.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    // ── Auth ───────────────────────────────────────────
    User   register(User user);
    String verify(User user);
    User   save(User user);

    // ── Lookup ─────────────────────────────────────────
    User   findByEmail(String email);
    User   findByResetToken(String token);
    User   getUserByUsername(String username);

    // ── CRUD ───────────────────────────────────────────
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO       getUserById(Long id);
    UserResponseDTO       createUser(UserRequestDTO dto);
    UserResponseDTO       updateUser(Long id, UserRequestDTO dto);
    void                  deleteUser(Long id);
}

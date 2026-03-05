package com.parvej.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Auth lookups
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String resetToken);

    // Existence checks for create
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    // Existence checks for update (exclude own id)
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByUsernameAndIdNot(String username, Long id);

    // Clean up expired reset tokens (optional — call via scheduler)
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.resetToken = null, u.resetTokenExpiry = null WHERE u.resetTokenExpiry < :now")
    void clearExpiredResetTokens(long now);
}
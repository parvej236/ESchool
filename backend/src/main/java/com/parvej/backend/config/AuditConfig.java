package com.parvej.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {
    @Bean
    public AuditorAware<Long> auditorProvider() {
        // Returns 0L as system user until you wire in real auth/JWT
        // Later: replace with SecurityContextHolder to get logged-in user id
        return () -> Optional.of(0L);
    }
}

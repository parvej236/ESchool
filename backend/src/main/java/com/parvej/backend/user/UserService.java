package com.parvej.backend.user;

import com.parvej.backend.config.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    public Users register(Users user) {
        // Check if username already exists
        Optional<Users> existingUser = repo.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        return repo.save(user);
    }

    public String verify(Users user) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        if(authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getUsername());
        }
        return "fail";
    }

    // Admin methods
    public List<Users> getAllUsers() {
        return repo.findAll();
    }

    public Users getUserById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public Users getUserByUsername(String username) {
        return repo.findByUsername(username).orElse(null);
    }

    public Users updateUser(Users user) {
        return repo.save(user);
    }

    public void deleteUser(Integer id) {
        repo.deleteById(id);
    }

    public Users findByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }

    public Users findByResetToken(String token) {
        return repo.findByResetToken(token).orElse(null);
    }

    public Users save(Users user) {
        return repo.save(user);
    }
}

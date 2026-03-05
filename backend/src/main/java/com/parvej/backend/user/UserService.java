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

    public User register(User user) {
        // Check if username already exists
        Optional<User> existingUser = repo.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        return repo.save(user);
    }

    public String verify(User user) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        if(authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getUsername());
        }
        return "fail";
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User getUserById(Integer id) {
        return repo.findById(id).orElse(null);
    }

    public User getUserByUsername(String username) {
        return repo.findByUsername(username).orElse(null);
    }

    public User updateUser(User user) {
        return repo.save(user);
    }

    public void deleteUser(Integer id) {
        repo.deleteById(id);
    }

    public User findByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }

    public User findByResetToken(String token) {
        return repo.findByResetToken(token).orElse(null);
    }

    public User save(User user) {
        return repo.save(user);
    }
}

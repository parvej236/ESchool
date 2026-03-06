package com.parvej.backend.user;

import com.parvej.backend.config.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository        repo;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    // ── Auth ───────────────────────────────────────────

    @Override
    public User register(User user) {
        if (repo.existsByUsername(user.getUsername()))
            throw new RuntimeException("Username already exists");
        if (repo.existsByEmail(user.getEmail()))
            throw new RuntimeException("Email already in use");
        return repo.save(user);
    }

    @Override
    public String verify(User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(user.getUsername());
        }
        return "fail";
    }

    @Override
    public User save(User user) {
        return repo.save(user);
    }

    // ── Lookup ─────────────────────────────────────────

    @Override
    public User findByEmail(String email) {
        return repo.findByEmail(email).orElse(null);
    }

    @Override
    public User findByResetToken(String token) {
        return repo.findByResetToken(token).orElse(null);
    }

    @Override
    public User getUserByUsername(String username) {
        return repo.findByUsername(username).orElse(null);
    }

    // ── CRUD ───────────────────────────────────────────

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return repo.findAll()
                .stream()
                .map(UserResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        return UserResponseDTO.from(findOrThrow(id));
    }

    @Override
    public UserResponseDTO createUser(UserRequestDTO dto) {
        if (repo.existsByEmail(dto.getEmail()))
            throw new RuntimeException("Email is already in use");
        if (repo.existsByUsername(dto.getUsername()))
            throw new RuntimeException("Username is already taken");
        if (dto.getPassword() == null || dto.getPassword().isBlank())
            throw new RuntimeException("Password is required");

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return UserResponseDTO.from(repo.save(user));
    }

    @Override
    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User user = findOrThrow(id);

        if (repo.existsByEmailAndIdNot(dto.getEmail(), id))
            throw new RuntimeException("Email is already in use by another user");
        if (repo.existsByUsernameAndIdNot(dto.getUsername(), id))
            throw new RuntimeException("Username is already taken by another user");

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank())
            user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return UserResponseDTO.from(repo.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        if (!repo.existsById(id))
            throw new RuntimeException("User not found with id: " + id);
        repo.deleteById(id);
    }

    // ── Private ────────────────────────────────────────

    private User findOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
}

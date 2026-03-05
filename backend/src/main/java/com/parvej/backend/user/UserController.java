package com.parvej.backend.user;

import com.parvej.backend.common.ApiResponse;
import com.parvej.backend.dto.UserRequestDTO;
import com.parvej.backend.dto.UserResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/csrf-token")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute("_csrf");
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getAllUsers() {
        try {
            List<UserResponseDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(
                    ApiResponse.success("Users fetched successfully", users)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(
                    ApiResponse.success("User fetched successfully", userService.getUserById(id))
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserResponseDTO>> createUser(
            @Valid @RequestBody UserRequestDTO dto
    ) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    ApiResponse.success("User created successfully", userService.createUser(dto))
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO dto
    ) {
        try {
            return ResponseEntity.ok(
                    ApiResponse.success("User updated successfully", userService.updateUser(id, dto))
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}

package com.parvej.backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService service;

    @GetMapping("/all")
    public ResponseEntity<?> getUsers() {
        try {
            List<User> users = service.getAllUsers();
            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "data", users,
                            "message", "Users fetched successfully"
                    )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
        }
    }


}

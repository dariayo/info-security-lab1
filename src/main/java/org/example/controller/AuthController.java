package org.example.controller;

import jakarta.validation.Valid;
import org.example.DTO.AuthResponse;
import org.example.DTO.LoginRequest;
import org.example.DTO.RegisterRequest;
import org.example.models.User;
import org.example.services.JwtService;
import org.example.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        if (userService.usernameExists(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if (userService.emailExists(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = userService.createUser(
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getEmail()
        );

        String token = jwtService.generateToken(user.getUsername());

        return ResponseEntity.ok(new AuthResponse(token, user.getUsername()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Optional<User> userOpt = userService.validateUser(loginRequest.getUsername(), loginRequest.getPassword());

        if (userOpt.isPresent()) {
            String token = jwtService.generateToken(loginRequest.getUsername());
            return ResponseEntity.ok(new AuthResponse(token, loginRequest.getUsername()));
        }

        return ResponseEntity.status(401).body("Invalid credentials");
    }
}

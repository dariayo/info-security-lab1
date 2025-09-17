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
import org.springframework.web.util.HtmlUtils;

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
        String escapedUsername = HtmlUtils.htmlEscape(registerRequest.getUsername());
        String escapedEmail = HtmlUtils.htmlEscape(registerRequest.getEmail());

        if (userService.usernameExists(escapedUsername)) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if (userService.emailExists(escapedEmail)) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = userService.createUser(
                escapedUsername,
                registerRequest.getPassword(),
                escapedEmail
        );

        String token = jwtService.generateToken(user.getUsername());

        return ResponseEntity.ok(new AuthResponse(token, HtmlUtils.htmlEscape(user.getUsername())));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        String escapedUsername = HtmlUtils.htmlEscape(loginRequest.getUsername());

        Optional<User> userOpt = userService.validateUser(escapedUsername, loginRequest.getPassword());

        if (userOpt.isPresent()) {
            String token = jwtService.generateToken(escapedUsername);
            return ResponseEntity.ok(new AuthResponse(token, HtmlUtils.htmlEscape(escapedUsername)));
        }

        return ResponseEntity.status(401).body("Invalid credentials");
    }
}
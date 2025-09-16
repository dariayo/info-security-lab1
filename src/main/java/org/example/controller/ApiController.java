package org.example.controller;

import org.example.models.DataItem;
import org.example.models.User;
import org.example.repository.DataItemRepository;
import org.example.repository.UserRepository;
import org.example.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private DataItemRepository dataItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @GetMapping("/data")
    public ResponseEntity<?> getData(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Authorization header required");
        }

        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(401).body("Invalid token");
        }

        String username = jwtService.getUsernameFromToken(token);
        List<DataItem> items = dataItemRepository.findByUserUsername(username);

        return ResponseEntity.ok(items);
    }

    @PostMapping("/data")
    public ResponseEntity<?> createDataItem(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody DataItem dataItem) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Authorization header required");
        }

        String token = authHeader.substring(7);
        if (!jwtService.validateToken(token)) {
            return ResponseEntity.status(401).body("Invalid token");
        }

        String username = jwtService.getUsernameFromToken(token);
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            dataItem.setUser(userOpt.get());
            DataItem savedItem = dataItemRepository.save(dataItem);
            return ResponseEntity.ok(savedItem);
        }

        return ResponseEntity.status(404).body("User not found");
    }
}

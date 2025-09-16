package org.example.controller;

import org.example.models.DataItem;
import org.example.models.User;
import org.example.repository.DataItemRepository;
import org.example.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final DataItemRepository dataItemRepository;
    private final UserRepository userRepository;

    public ApiController(DataItemRepository dataItemRepository, UserRepository userRepository) {
        this.dataItemRepository = dataItemRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/data")
    public ResponseEntity<?> getData(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String username = authentication.getName();
        List<DataItem> items = dataItemRepository.findByUserUsername(username);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/data")
    public ResponseEntity<?> createDataItem(Authentication authentication, @RequestBody DataItem dataItem) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String username = authentication.getName();
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            dataItem.setUser(userOpt.get());
            DataItem savedItem = dataItemRepository.save(dataItem);
            return ResponseEntity.ok(savedItem);
        }

        return ResponseEntity.status(404).body("User not found");
    }
}

package org.example.controller;

import org.example.DTO.DataItemDto;
import org.example.models.DataItem;
import org.example.models.User;
import org.example.repository.DataItemRepository;
import org.example.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

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

        List<DataItemDto> items = dataItemRepository.findByUserUsername(username)
                .stream()
                .map(item -> new DataItemDto(
                        item.getId(),
                        item.getTitle(),
                        item.getDescription()
                ))
                .toList();

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
            dataItem.setTitle(HtmlUtils.htmlEscape(dataItem.getTitle()));
            if (dataItem.getDescription() != null) {
                dataItem.setDescription(HtmlUtils.htmlEscape(dataItem.getDescription()));
            }

            dataItem.setUser(userOpt.get());
            DataItem savedItem = dataItemRepository.save(dataItem);

            return ResponseEntity.ok(savedItem);
        }

        return ResponseEntity.status(404).body("User not found");
    }
}
package org.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataItemDto {
    private Long id;
    private String title;
    private String description;
}


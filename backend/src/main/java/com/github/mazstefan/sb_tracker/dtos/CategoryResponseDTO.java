package com.github.mazstefan.sb_tracker.dtos;

import com.github.mazstefan.sb_tracker.entities.enums.CategoryType;

public class CategoryResponseDTO {
    
    private Long id;
    private String name;
    private CategoryType type;

    public CategoryResponseDTO(Long id, String name, CategoryType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Long getId() { return id; }

    public String getName() { return name; }

    public CategoryType getType() { return type; }
}

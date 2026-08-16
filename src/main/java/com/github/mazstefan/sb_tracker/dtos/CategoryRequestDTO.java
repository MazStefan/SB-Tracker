package com.github.mazstefan.sb_tracker.dtos;

import com.github.mazstefan.sb_tracker.entities.enums.CategoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CategoryRequestDTO {
    
    @NotBlank(message = "Category name cannot be empty")
    private String name;

    @NotNull(message = "Category type (INCOME?/EXPENSE) is required")
    private CategoryType type;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CategoryType getType() { return type; }
    public void setType(CategoryType type) { this.type = type; }
}

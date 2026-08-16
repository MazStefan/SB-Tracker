package com.github.mazstefan.sb_tracker.controllers;

import com.github.mazstefan.sb_tracker.dtos.CategoryRequestDTO;
import com.github.mazstefan.sb_tracker.dtos.CategoryResponseDTO;
import com.github.mazstefan.sb_tracker.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/category")
public class CategoryController {
    
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @Valid @RequestBody CategoryRequestDTO requestDTO) {
        //TODO: Replace with real JWT token generation in week 2
        Long currentUserId = 1L;

        CategoryResponseDTO createdCategory = categoryService.createCategory(requestDTO, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getMyCategories() {
        //TODO: Replace with real JWT token generation in week 2
        Long currentUserId = 1L;

        List<CategoryResponseDTO> categories = categoryService.getUserCategories(currentUserId);

        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id, 
            @Valid @RequestBody CategoryRequestDTO requestDTO) {
        //TODO: Replace with real JWT token generation in week 2
        Long currentUserId = 1L;

        CategoryResponseDTO updatedCategory = categoryService.updateCategory(requestDTO, id, currentUserId);
        
        return ResponseEntity.ok(updatedCategory);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        //TODO: Replace with real JWT token generation in week 2
        Long currentUserId = 1L;

        categoryService.deleteCategory(id, currentUserId);

        return ResponseEntity.noContent().build(); 
    }
}

package com.github.mazstefan.sb_tracker.controllers;

import com.github.mazstefan.sb_tracker.dtos.CategoryRequestDTO;
import com.github.mazstefan.sb_tracker.dtos.CategoryResponseDTO;
import com.github.mazstefan.sb_tracker.services.CategoryService;
import com.github.mazstefan.sb_tracker.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(
            Authentication authentication,
            @Valid @RequestBody CategoryRequestDTO requestDTO) {

        Long currentUserId = extractUserId(authentication);

        CategoryResponseDTO createdCategory = categoryService.createCategory(requestDTO, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getMyCategories(Authentication authentication) {
        Long currentUserId = extractUserId(authentication);

        List<CategoryResponseDTO> categories = categoryService.getUserCategories(currentUserId);

        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            Authentication authentication,
            @PathVariable Long id, 
            @Valid @RequestBody CategoryRequestDTO requestDTO) {

        Long currentUserId = extractUserId(authentication);

        CategoryResponseDTO updatedCategory = categoryService.updateCategory(requestDTO, id, currentUserId);
        
        return ResponseEntity.ok(updatedCategory);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            Authentication authentication,
            @PathVariable Long id) {

        Long currentUserId = extractUserId(authentication);

        categoryService.deleteCategory(id, currentUserId);

        return ResponseEntity.noContent().build(); 
    }

    private Long extractUserId(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return userDetails.getId();
    }
}

package com.github.mazstefan.sb_tracker.services;

import com.github.mazstefan.sb_tracker.dtos.CategoryRequestDTO;
import com.github.mazstefan.sb_tracker.dtos.CategoryResponseDTO;
import com.github.mazstefan.sb_tracker.entities.Category;
import com.github.mazstefan.sb_tracker.entities.User;
import com.github.mazstefan.sb_tracker.repositories.CategoryRepository;
import com.github.mazstefan.sb_tracker.repositories.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (categoryRepository.existsByUserIdAndNameAndType(userId, requestDTO.getName(), requestDTO.getType())) {
            throw new RuntimeException("A category with this name and type already exists.");
        }

        Category category = new Category();
        category.setName(requestDTO.getName());
        category.setType(requestDTO.getType());
        category.setUser(user);

        Category savedCategory = categoryRepository.save(category);

        return mapToResponseDTO(savedCategory);
    }

    public List<CategoryResponseDTO> getUserCategories(Long userId) {
        List<Category> categories = categoryRepository.findAllByUserId(userId);

        return categories.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public CategoryResponseDTO updateCategory(CategoryRequestDTO requestDTO, Long categoryId, Long userId) {
        Category existingCategory = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (categoryRepository.existsByUserIdAndNameAndTypeAndIdNot(userId, requestDTO.getName(), requestDTO.getType(), categoryId)) {
            throw new RuntimeException("A category with this name and type already exists.");
        }

        existingCategory.setName(requestDTO.getName());
        existingCategory.setType(requestDTO.getType());

        Category updatedCategory = categoryRepository.save(existingCategory);

        return mapToResponseDTO(updatedCategory);
    }

    public void deleteCategory(Long categoryId, Long userId) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        categoryRepository.delete(category);
    }

    private CategoryResponseDTO mapToResponseDTO(Category category) {
        return new CategoryResponseDTO(
                category.getId(), 
                category.getName(), 
                category.getType()
        );
    }
}

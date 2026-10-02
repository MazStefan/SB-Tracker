package com.github.mazstefan.sb_tracker.services;

import com.github.mazstefan.sb_tracker.dtos.CategoryRequestDTO;
import com.github.mazstefan.sb_tracker.dtos.CategoryResponseDTO;
import com.github.mazstefan.sb_tracker.entities.Category;
import com.github.mazstefan.sb_tracker.entities.User;
import com.github.mazstefan.sb_tracker.entities.enums.CategoryType;
import com.github.mazstefan.sb_tracker.repositories.CategoryRepository;
import com.github.mazstefan.sb_tracker.repositories.UserRepository;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public CategoryService(CategoryRepository categoryRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
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

        boolean isAdmin = user.getRole().name().equals("ADMIN");

        boolean includeOwnerInfo = isAdmin || user.getGroup() != null;

        if (user.getGroup() != null) {
            String destination = "/topic/group/" + user.getGroup().getId();
            
            Map<String, String> payload = Map.of("action", "REFRESH_TRANSACTIONS");
            messagingTemplate.convertAndSend(destination, payload);
        }

        return mapToResponseDTO(savedCategory, includeOwnerInfo);
    }

    public List<CategoryResponseDTO> getUserCategories(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Category> categories;

        boolean isAdmin = user.getRole().name().equals("ADMIN");

        if (isAdmin) {

            categories = categoryRepository.findAll();
        
        } else {

            categories = findAllCategoryForUserContext(user);

        }

        boolean includeOwnerInfo = isAdmin || user.getGroup() != null;

        return categories.stream()
                .map(category -> mapToResponseDTO(category, includeOwnerInfo))
                .collect(Collectors.toList());
    }

    public CategoryResponseDTO updateCategory(CategoryRequestDTO requestDTO, Long categoryId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category existingCategory = findCategoryForUserContext(categoryId, user);

        if (categoryExistsInContext(requestDTO.getName(), requestDTO.getType(), categoryId, user))
            throw new RuntimeException("A category with this name and type already exists.");

        existingCategory.setName(requestDTO.getName());
        existingCategory.setType(requestDTO.getType());

        Category updatedCategory = categoryRepository.save(existingCategory);

        boolean isAdmin = user.getRole().name().equals("ADMIN");

        boolean includeOwnerInfo = isAdmin || user.getGroup() != null;

        if (user.getGroup() != null) {
            String destination = "/topic/group/" + user.getGroup().getId();
            
            Map<String, String> payload = Map.of("action", "REFRESH_TRANSACTIONS");
            messagingTemplate.convertAndSend(destination, payload);
        }

        return mapToResponseDTO(updatedCategory, includeOwnerInfo);
    }

    public void deleteCategory(Long categoryId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Category category = findCategoryForUserContext(categoryId, user);
        
        categoryRepository.delete(category);

        if (user.getGroup() != null) {
            String destination = "/topic/group/" + user.getGroup().getId();
            
            Map<String, String> payload = Map.of("action", "REFRESH_TRANSACTIONS");
            messagingTemplate.convertAndSend(destination, payload);
        }
    }

    private Category findCategoryForUserContext(Long categoryId, User user) {
        if (user.getGroup() != null) {
            return categoryRepository.findByIdAndUserGroupId(categoryId, user.getGroup().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found in group"));
        }
        return categoryRepository.findByIdAndUserId(categoryId, user.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    private List<Category> findAllCategoryForUserContext(User user) {
        if (user.getGroup() != null) {
            return categoryRepository.findAllByUserGroupId(user.getGroup().getId());
        }
        return categoryRepository.findAllByUserId(user.getId());
    }

    private boolean categoryExistsInContext(String name, CategoryType type, Long excludeId, User user) {
        if (user.getGroup() != null) {
            return categoryRepository.existsByUserGroupIdAndNameAndTypeAndIdNot(
                    user.getGroup().getId(), name, type, excludeId);
        }
        return categoryRepository.existsByUserIdAndNameAndTypeAndIdNot(
                user.getId(), name, type, excludeId);
    }

    private CategoryResponseDTO mapToResponseDTO(Category category, boolean includeOwnerInfo) {
        String ownerEmail = includeOwnerInfo ? category.getUser().getEmail() : null;

        return new CategoryResponseDTO(
            category.getId(), 
            category.getName(), 
            category.getType(),
            ownerEmail
        );
    }
}

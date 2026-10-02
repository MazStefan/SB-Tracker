package com.github.mazstefan.sb_tracker.services;

import com.github.mazstefan.sb_tracker.dtos.BudgetRequestDTO;
import com.github.mazstefan.sb_tracker.dtos.BudgetResponseDTO;
import com.github.mazstefan.sb_tracker.entities.Budget;
import com.github.mazstefan.sb_tracker.entities.Category;
import com.github.mazstefan.sb_tracker.entities.User;
import com.github.mazstefan.sb_tracker.repositories.BudgetRepository;
import com.github.mazstefan.sb_tracker.repositories.CategoryRepository;
import com.github.mazstefan.sb_tracker.repositories.UserRepository;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BudgetService {
    
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public BudgetService(BudgetRepository budgetRepository, CategoryRepository categoryRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public BudgetResponseDTO createBudget(BudgetRequestDTO requestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = findCategoryForUserContext(requestDTO.getCategoryId(), user);

        if (newBudgetExistsInContext(category.getId(), requestDTO.getMonthYear(), user)) {
            throw new RuntimeException("A budget for this category and month already exists!");
        }  
                
        Budget budget = new Budget();
        budget.setMonthlyLimit(requestDTO.getMonthlyLimit());
        budget.setMonthYear(requestDTO.getMonthYear());
        budget.setUser(user);
        budget.setCategory(category);

        Budget savedBudget = budgetRepository.save(budget);

        boolean isAdmin = user.getRole().name().equals("ADMIN");

        boolean includeOwnerInfo = isAdmin || user.getGroup() != null;

        if (user.getGroup() != null) {
            String destination = "/topic/group/" + user.getGroup().getId();
            
            Map<String, String> payload = Map.of("action", "REFRESH_TRANSACTIONS");
            messagingTemplate.convertAndSend(destination, payload);
        }

        return mapToResponseDTO(savedBudget, includeOwnerInfo);
    }

    public List<BudgetResponseDTO> getUserBudgets(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Budget> budgets;

        boolean isAdmin = user.getRole().name().equals("ADMIN");

        if (isAdmin) {

            budgets = budgetRepository.findAll();

        } else {

            budgets = findAllBudgetForUserContext(user);

        }

        boolean includeOwnerInfo = isAdmin || user.getGroup() != null;

        return budgets.stream()
                .map(budget -> mapToResponseDTO(budget, includeOwnerInfo))
                .collect(Collectors.toList());
    }

    public BudgetResponseDTO getBudgetById(Long budgetId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Budget budget = findBudgetForUserContext(budgetId, user);

        boolean isAdmin = user.getRole().name().equals("ADMIN");

        boolean includeOwnerInfo = isAdmin || user.getGroup() != null;

        return mapToResponseDTO(budget, includeOwnerInfo);
    }

    public BudgetResponseDTO updateBudget(BudgetRequestDTO requestDTO, Long budgetId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Budget existingBudget = findBudgetForUserContext(budgetId, user);

        Category category = findCategoryForUserContext(requestDTO.getCategoryId(), user);

        if (budgetExistsInContext(userId, existingBudget.getMonthYear(), budgetId, user)) {
            throw new RuntimeException("A budget for this category and month already exists!");
        }    
        
        existingBudget.setMonthlyLimit(requestDTO.getMonthlyLimit());
        existingBudget.setMonthYear(requestDTO.getMonthYear());
        existingBudget.setCategory(category);

        Budget updatedBudget = budgetRepository.save(existingBudget);

        boolean isAdmin = user.getRole().name().equals("ADMIN");

        boolean includeOwnerInfo = isAdmin || user.getGroup() != null;

        if (user.getGroup() != null) {
            String destination = "/topic/group/" + user.getGroup().getId();
            
            Map<String, String> payload = Map.of("action", "REFRESH_TRANSACTIONS");
            messagingTemplate.convertAndSend(destination, payload);
        }

        return mapToResponseDTO(updatedBudget, includeOwnerInfo);
    }

    public void deleteBudget(Long budgetId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Budget budget = findBudgetForUserContext(budgetId, user);

        budgetRepository.delete(budget);

        if (user.getGroup() != null) {
            String destination = "/topic/group/" + user.getGroup().getId();
            
            Map<String, String> payload = Map.of("action", "REFRESH_TRANSACTIONS");
            messagingTemplate.convertAndSend(destination, payload);
        }
    }

    private Budget findBudgetForUserContext(Long budgetId, User user) {
        if (user.getGroup() != null)
            return budgetRepository.findByIdAndUserGroupId(budgetId, user.getGroup().getId())
                    .orElseThrow(() -> new RuntimeException("Budget not found"));
        else
            return budgetRepository.findByIdAndUserId(budgetId, user.getId())
                    .orElseThrow(() -> new RuntimeException("Budget not found"));
    }

    private List<Budget> findAllBudgetForUserContext(User user) {
        if (user.getGroup() != null)
            return budgetRepository.findAllByUserGroupId(user.getGroup().getId());
        else
            return budgetRepository.findAllByUserId(user.getId());
    }

    private Category findCategoryForUserContext(Long categoryId, User user) {
        if (user.getGroup() != null) {
            return categoryRepository.findByIdAndUserGroupId(categoryId, user.getGroup().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found in group"));
        }
        return categoryRepository.findByIdAndUserId(categoryId, user.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    private boolean budgetExistsInContext(Long categoryId, LocalDate monthYear, Long budgetId, User user) {
        if (user.getGroup() != null) {
            return budgetRepository.existsByUserGroupIdAndCategoryIdAndMonthYearAndIdNot(user.getGroup().getId(), categoryId, monthYear, budgetId);
        } 
        return budgetRepository.existsByUserIdAndCategoryIdAndMonthYearAndIdNot(user.getId(), categoryId, monthYear, budgetId);
    }

    private boolean newBudgetExistsInContext(Long categoryId, LocalDate monthYear, User user) {
        if (user.getGroup() != null) {
            return budgetRepository.existsByUserGroupIdAndCategoryIdAndMonthYear(user.getGroup().getId(), categoryId, monthYear);
        } 
        return budgetRepository.existsByUserIdAndCategoryIdAndMonthYear(user.getId(), categoryId, monthYear);
    }

    private BudgetResponseDTO mapToResponseDTO(Budget budget, boolean includeOwnerInfo) {
        String ownerEmail = includeOwnerInfo ? budget.getUser().getEmail() : null;

        return new BudgetResponseDTO(
                budget.getId(),
                budget.getMonthlyLimit(),
                budget.getMonthYear(),
                budget.getCategory().getName(),
                budget.getCategory().getType().name(),
                ownerEmail
            );
    
    }
}

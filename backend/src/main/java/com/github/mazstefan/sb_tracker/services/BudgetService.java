package com.github.mazstefan.sb_tracker.services;

import com.github.mazstefan.sb_tracker.dtos.BudgetRequestDTO;
import com.github.mazstefan.sb_tracker.dtos.BudgetResponseDTO;
import com.github.mazstefan.sb_tracker.entities.Budget;
import com.github.mazstefan.sb_tracker.entities.Category;
import com.github.mazstefan.sb_tracker.entities.User;
import com.github.mazstefan.sb_tracker.repositories.BudgetRepository;
import com.github.mazstefan.sb_tracker.repositories.CategoryRepository;
import com.github.mazstefan.sb_tracker.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BudgetService {
    
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public BudgetService(BudgetRepository budgetRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public BudgetResponseDTO createBudget(BudgetRequestDTO requestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findByIdAndUserId(requestDTO.getCategoryId(), userId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (budgetRepository.existsByUserIdAndCategoryIdAndMonthYear(userId, requestDTO.getCategoryId(), requestDTO.getMonthYear())) {
            throw new RuntimeException("A budget for this category and month already exists!");
        }  
                
        Budget budget = new Budget();
        budget.setMonthlyLimit(requestDTO.getMonthlyLimit());
        budget.setMonthYear(requestDTO.getMonthYear());
        budget.setUser(user);
        budget.setCategory(category);

        Budget savedBudget = budgetRepository.save(budget);

        return mapToResponseDTO(savedBudget, false);
    }

    public List<BudgetResponseDTO> getUserBudgets(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Budget> budgets;

        boolean isAdmin = user.getRole().name().equals("ADMIN");

        if (isAdmin) {

            budgets = budgetRepository.findAll();

        } else {

            budgets = budgetRepository.findAllByUserId(userId);

        }

        return budgets.stream()
                .map(budget -> mapToResponseDTO(budget, isAdmin))
                .collect(Collectors.toList());
    }

    public BudgetResponseDTO getBudgetById(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        return mapToResponseDTO(budget, false);
    }

    public BudgetResponseDTO updateBudget(BudgetRequestDTO requestDTO, Long budgetId, Long userId) {
        Budget existingBudget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        Category category = categoryRepository.findByIdAndUserId(requestDTO.getCategoryId(), userId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (budgetRepository.existsByUserIdAndCategoryIdAndMonthYearAndIdNot(userId, requestDTO.getCategoryId(), requestDTO.getMonthYear(), budgetId)) {
            throw new RuntimeException("A budget for this category and month already exists!");
        } 
        
        existingBudget.setMonthlyLimit(requestDTO.getMonthlyLimit());
        existingBudget.setMonthYear(requestDTO.getMonthYear());
        existingBudget.setCategory(category);

        Budget updatedBudget = budgetRepository.save(existingBudget);

        return mapToResponseDTO(updatedBudget, false);
    }

    public void deleteBudget(Long budgetId, Long userId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        budgetRepository.delete(budget);
    }

    private BudgetResponseDTO mapToResponseDTO(Budget budget, boolean isAdmin) {
        if(isAdmin)
            return new BudgetResponseDTO(
                    budget.getId(),
                    budget.getMonthlyLimit(),
                    budget.getMonthYear(),
                    budget.getCategory().getName(),
                    budget.getCategory().getType().name(),
                    budget.getUser().getEmail()
            );
        else
            return new BudgetResponseDTO(
                    budget.getId(),
                    budget.getMonthlyLimit(),
                    budget.getMonthYear(),
                    budget.getCategory().getName(),
                    budget.getCategory().getType().name()
            );
    }
}

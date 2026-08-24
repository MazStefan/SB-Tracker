package com.github.mazstefan.sb_tracker.controllers;

import com.github.mazstefan.sb_tracker.dtos.BudgetRequestDTO;
import com.github.mazstefan.sb_tracker.dtos.BudgetResponseDTO;
import com.github.mazstefan.sb_tracker.services.BudgetService;
import com.github.mazstefan.sb_tracker.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<BudgetResponseDTO> createBudget(
            Authentication authentication,
            @Valid @RequestBody BudgetRequestDTO requestDTO) {

        Long currentUserId = extractUserId(authentication);

        BudgetResponseDTO createdBudget = budgetService.createBudget(requestDTO, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdBudget);
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponseDTO>> getMyBudgets(Authentication authentication) {
        Long currentUserId = extractUserId(authentication);

        List<BudgetResponseDTO> budgets = budgetService.getUserBudgets(currentUserId);

        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponseDTO> getMyBudget(
            Authentication authentication,
            @PathVariable Long id) {

        Long currentUserId = extractUserId(authentication);

        BudgetResponseDTO budget = budgetService.getBudgetById(id, currentUserId);

        return ResponseEntity.ok(budget);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponseDTO> updateBudget(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequestDTO requestDTO) {

        Long currentUserId = extractUserId(authentication);

        BudgetResponseDTO updatedBudget = budgetService.updateBudget(requestDTO, id, currentUserId);

        return ResponseEntity.ok(updatedBudget);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(
            Authentication authentication,
            @PathVariable Long id) {

        Long currentUserId = extractUserId(authentication);

        budgetService.deleteBudget(id, currentUserId);

        return ResponseEntity.noContent().build();
    }

    private Long extractUserId(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }
}

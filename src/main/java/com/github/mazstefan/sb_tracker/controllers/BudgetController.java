package com.github.mazstefan.sb_tracker.controllers;

import com.github.mazstefan.sb_tracker.dtos.BudgetRequestDTO;
import com.github.mazstefan.sb_tracker.dtos.BudgetResponseDTO;
import com.github.mazstefan.sb_tracker.services.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
            @Valid @RequestBody BudgetRequestDTO requestDTO) {
        //TODO: Replace with real JWT token generation in week 2
        Long currentUserId = 1L;

        BudgetResponseDTO createdBudget = budgetService.createBudget(requestDTO, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdBudget);
    }

    @GetMapping
    public ResponseEntity<List<BudgetResponseDTO>> getMyBudgets() {
        //TODO: Replace with real JWT token generation in week 2
        Long currentUserId = 1L;

        List<BudgetResponseDTO> budgets = budgetService.getUserBudgets(currentUserId);

        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponseDTO> getMyBudget(@PathVariable Long id) {
        //TODO: Replace with real JWT token generation in week 2
        Long currentUserId = 1L;

        BudgetResponseDTO budget = budgetService.getBudgetById(id, currentUserId);

        return ResponseEntity.ok(budget);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponseDTO> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequestDTO requestDTO) {
        //TODO: Replace with real JWT token generation in week 2
        Long currentUserId = 1L;

        BudgetResponseDTO updatedBudget = budgetService.updateBudget(requestDTO, id, currentUserId);

        return ResponseEntity.ok(updatedBudget);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        //TODO: Replace with real JWT token generation in week 2
        Long currentUserId = 1L;

        budgetService.deleteBudget(id, currentUserId);

        return ResponseEntity.noContent().build();
    }
}

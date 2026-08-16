package com.github.mazstefan.sb_tracker.repositories;

import com.github.mazstefan.sb_tracker.entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    Optional<Budget> findByUserIdAndCategoryId(Long userId, Long categoryId);

    List<Budget> findAllByUserId(Long userId);

    Optional<Budget> findByIdAndUserId(Long budgetId, Long userId);

    boolean existsByUserIdAndCategoryIdAndMonthYear(Long userId, Long categoryId, LocalDate monthYear);
}

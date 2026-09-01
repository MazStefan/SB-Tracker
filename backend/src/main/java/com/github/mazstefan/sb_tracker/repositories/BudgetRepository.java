package com.github.mazstefan.sb_tracker.repositories;

import com.github.mazstefan.sb_tracker.entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT b.monthlyLimit FROM Budget b WHERE b.user.id = :userId AND b.category.id = :categoryId AND MONTH(b.monthYear) = :month AND YEAR(b.monthYear) = :year")
    Optional<Double> findLimitByYearAndMonth(
        @Param("userId") Long userId, 
        @Param("categoryId") Long categoryId, 
        @Param("month") int month, 
        @Param("year") int year
    );

    boolean existsByUserIdAndCategoryIdAndMonthYearAndIdNot(Long userId, Long categoryId, LocalDate monthYear, Long id);
}

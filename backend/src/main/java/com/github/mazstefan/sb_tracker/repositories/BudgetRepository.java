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

    Optional<Budget> findByUserGroupIdAndCategoryId(Long groupId, Long categoryId);

    List<Budget> findAllByUserId(Long userId);

    List<Budget> findAllByUserGroupId(Long groupId);

    List<Budget> findAllByUserIdAndMonthYearBetween(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT b FROM Budget b WHERE b.user.group.id = :groupId AND b.monthYear >= :startDate AND b.monthYear <= :endDate")
    List<Budget> findAllByGroupIdAndMonthYearBetween(
        @Param("groupId") Long groupId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    List<Budget> findAllByMonthYearBetween(LocalDate startDate, LocalDate endDate);

    Optional<Budget> findByIdAndUserId(Long budgetId, Long userId);

    Optional<Budget> findByIdAndUserGroupId(Long budgetId, Long groupId);

    boolean existsByUserIdAndCategoryIdAndMonthYear(Long userId, Long categoryId, LocalDate monthYear);

    boolean existsByUserGroupIdAndCategoryIdAndMonthYear(Long groupId, Long categoryId, LocalDate monthYear);

    @Query("SELECT b.monthlyLimit FROM Budget b WHERE b.user.id = :userId AND b.category.id = :categoryId AND MONTH(b.monthYear) = :month AND YEAR(b.monthYear) = :year")
    Optional<Double> findLimitByYearAndMonth(
        @Param("userId") Long userId, 
        @Param("categoryId") Long categoryId, 
        @Param("month") int month, 
        @Param("year") int year
    );

    @Query("SELECT b.monthlyLimit FROM Budget b WHERE b.user.group.id = :groupId AND b.category.id = :categoryId AND MONTH(b.monthYear) = :month AND YEAR(b.monthYear) = :year")
    Optional<Double> findGroupLimitByYearAndMonth(
        @Param("groupId") Long groupId, 
        @Param("categoryId") Long categoryId, 
        @Param("month") int month, 
        @Param("year") int year
    );

    boolean existsByUserIdAndCategoryIdAndMonthYearAndIdNot(Long userId, Long categoryId, LocalDate monthYear, Long id);

    boolean existsByUserGroupIdAndCategoryIdAndMonthYearAndIdNot(Long groupId, Long categoryId, LocalDate monthYear, Long id);
}

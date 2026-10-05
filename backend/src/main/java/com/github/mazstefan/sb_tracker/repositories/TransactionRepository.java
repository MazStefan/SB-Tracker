package com.github.mazstefan.sb_tracker.repositories;

import com.github.mazstefan.sb_tracker.entities.Transaction;
import com.github.mazstefan.sb_tracker.dtos.CategorySpendDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findAllByUserId(Long id);

    List<Transaction> findAllByUserGroupId(Long id);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND t.category.id = :categoryId")
    Double sumAmountByUserIdAndCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.group.id = :groupId AND t.category.id = :categoryId")
    Double sumAmountByUserGroupIdAndCategoryId(@Param("groupId") Long groupId, @Param("categoryId") Long categoryId);

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    Optional<Transaction> findByIdAndUserGroupId(Long id, Long groupId);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND t.category.id = :categoryId AND MONTH(t.date) = :month AND YEAR(t.date) = :year")
    Optional<Double> sumTransactionsByCategoryAndMonth(
        @Param("userId") Long userId, 
        @Param("categoryId") Long categoryId, 
        @Param("month") int month, 
        @Param("year") int year
    );

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.group.id = :groupId AND t.category.id = :categoryId AND MONTH(t.date) = :month AND YEAR(t.date) = :year")
    Optional<Double> sumGroupTransactionsByCategoryAndMonth(
        @Param("groupId") Long groupId, 
        @Param("categoryId") Long categoryId, 
        @Param("month") int month, 
        @Param("year") int year
    );

    @Query("SELECT new com.github.mazstefan.sb_tracker.dtos.CategorySpendDTO(c.name, c.type, SUM(t.amount)) " +
            "FROM Transaction t " +
            "JOIN t.category c " +
            "WHERE t.user.id = :userId AND MONTH(t.date) = :month AND YEAR(t.date) = :year " +
            "GROUP BY c.name, c.type")
    List<CategorySpendDTO> getMonthlySpendReport(
            @Param("userId") Long userId, 
            @Param("month") int month, 
            @Param("year") int year
    );

    @Query("SELECT new com.github.mazstefan.sb_tracker.dtos.CategorySpendDTO(c.name, c.type, SUM(t.amount)) " +
            "FROM Transaction t " +
            "JOIN t.category c " +
            "WHERE t.user.group.id = :groupId AND MONTH(t.date) = :month AND YEAR(t.date) = :year " +
            "GROUP BY c.name, c.type")
    List<CategorySpendDTO> getGroupMonthlySpendReport(
            @Param("groupId") Long groupId,
            @Param("month") int month,
            @Param("year") int year
    );

    @Query("SELECT new com.github.mazstefan.sb_tracker.dtos.CategorySpendDTO(c.name, c.type, SUM(t.amount)) " +
            "FROM Transaction t " +
            "JOIN t.category c " +
            "WHERE MONTH(t.date) = :month AND YEAR(t.date) = :year " +
            "GROUP BY c.name, c.type")
    List<CategorySpendDTO> getAllMonthlySpendReport(
            @Param("month") int month, 
            @Param("year") int year
    );
}

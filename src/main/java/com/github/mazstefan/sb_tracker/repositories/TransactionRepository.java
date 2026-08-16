package com.github.mazstefan.sb_tracker.repositories;

import com.github.mazstefan.sb_tracker.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findAllByUserId(Long id);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND t.category.id = :categoryId")
    Double sumAmountByUserIdAndCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);
}

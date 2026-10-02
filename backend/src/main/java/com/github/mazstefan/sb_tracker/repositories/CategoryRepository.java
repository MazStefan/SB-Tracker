package com.github.mazstefan.sb_tracker.repositories;

import com.github.mazstefan.sb_tracker.entities.Category;
import com.github.mazstefan.sb_tracker.entities.enums.CategoryType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findAllByUserId(Long userId);

    List<Category> findAllByUserGroupId(Long groupId);

    Optional<Category> findByIdAndUserId(Long id, Long userId);

    Optional<Category> findByIdAndUserGroupId(Long id, Long groupId);

    Optional<Category> findByUserIdAndNameAndType(Long userId, String name, CategoryType type);

    boolean existsByUserIdAndNameAndType(Long userId, String name, CategoryType type);

    boolean existsByUserGroupIdAndNameAndType(Long groupId, String name, CategoryType type);

    boolean existsByUserIdAndNameAndTypeAndIdNot(Long userId, String name, CategoryType type, Long id);

    boolean existsByUserGroupIdAndNameAndTypeAndIdNot(Long groupId, String name, CategoryType type, Long id);
}

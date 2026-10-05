package com.github.mazstefan.sb_tracker.dtos;

import java.math.BigDecimal;

import com.github.mazstefan.sb_tracker.entities.enums.CategoryType;

public record CategorySpendDTO(String categoryName, String categoryType, BigDecimal totalSpent, BigDecimal budgetLimit, String ownerEmail) {

    public CategorySpendDTO(String categoryName, CategoryType categoryType, BigDecimal totalSpent) {
        this(categoryName, categoryType.name(), totalSpent, BigDecimal.ZERO, null); 
    }

    public CategorySpendDTO(String categoryName, CategoryType categoryType, BigDecimal totalSpent, String ownerEmail) {
        this(categoryName, categoryType.name(), totalSpent, BigDecimal.ZERO, ownerEmail); 
    }
    
    public CategorySpendDTO withLimit(BigDecimal limit) {
        return new CategorySpendDTO(this.categoryName, this.categoryType, this.totalSpent, limit, null);
    }

    public CategorySpendDTO withOwner(String email) {
        return new CategorySpendDTO(this.categoryName, this.categoryType, this.totalSpent, null, email);
    }

    public CategorySpendDTO withLimitOwner(BigDecimal limit, String email) {
        return new CategorySpendDTO(this.categoryName, this.categoryType, this.totalSpent, limit, email);
    }
}

package com.github.mazstefan.sb_tracker.dtos;

import java.math.BigDecimal;

import com.github.mazstefan.sb_tracker.entities.enums.CategoryType;

public record CategorySpendDTO(String categoryName, String categoryType, BigDecimal totalSpent, BigDecimal budgetLimit) {

    public CategorySpendDTO(String categoryName, CategoryType categoryType, BigDecimal totalSpent) {
        this(categoryName, categoryType.name(), totalSpent, BigDecimal.ZERO); 
    }
    
    public CategorySpendDTO withLimit(BigDecimal limit) {
        return new CategorySpendDTO(this.categoryName, this.categoryType, this.totalSpent, limit);
    }
    
}

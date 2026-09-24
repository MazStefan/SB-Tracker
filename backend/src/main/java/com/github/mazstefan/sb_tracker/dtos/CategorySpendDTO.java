package com.github.mazstefan.sb_tracker.dtos;

import java.math.BigDecimal;

public record CategorySpendDTO(String categoryName, BigDecimal totalSpent, BigDecimal budgetLimit) {

    public CategorySpendDTO(String categoryName, BigDecimal totalSpent) {
        this(categoryName, totalSpent, BigDecimal.ZERO); 
    }
    
    public CategorySpendDTO withLimit(BigDecimal limit) {
        return new CategorySpendDTO(this.categoryName, this.totalSpent, limit);
    }
    
}

package com.github.mazstefan.sb_tracker.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BudgetResponseDTO {
    
    private Long id;
    private BigDecimal monthlyLimit;
    private LocalDate monthYear;
    private String categoryName;

    public BudgetResponseDTO(Long id, BigDecimal monthlyLimit, LocalDate monthYear, String categoryName) {
        this.id = id;
        this.monthlyLimit = monthlyLimit;
        this.monthYear = monthYear;
        this.categoryName = categoryName;
    }

    public Long getId() { return id; }

    public BigDecimal getMonthlyLimit() { return monthlyLimit; }

    public LocalDate getMonthYear() { return monthYear; }

    public String getCategoryName() { return categoryName; }
}

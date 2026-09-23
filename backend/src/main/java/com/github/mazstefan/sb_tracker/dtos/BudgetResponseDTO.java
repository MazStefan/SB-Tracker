package com.github.mazstefan.sb_tracker.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BudgetResponseDTO {
    
    private Long id;
    private BigDecimal monthlyLimit;
    private LocalDate monthYear;
    private String categoryName;
    private String categoryType;
    private String ownerEmail;

    // USER CONSTRUCTOR
    public BudgetResponseDTO(Long id, BigDecimal monthlyLimit, LocalDate monthYear, String categoryName, String categoryType) {
        this.id = id;
        this.monthlyLimit = monthlyLimit;
        this.monthYear = monthYear;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
    }

    // ADMIN CONSTRUCTOR
    public BudgetResponseDTO(Long id, BigDecimal monthlyLimit, LocalDate monthYear, String categoryName, String categoryType, String ownerEmail) {
        this.id = id;
        this.monthlyLimit = monthlyLimit;
        this.monthYear = monthYear;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.ownerEmail = ownerEmail;
    }

    public Long getId() { return id; }

    public BigDecimal getMonthlyLimit() { return monthlyLimit; }

    public LocalDate getMonthYear() { return monthYear; }

    public String getCategoryName() { return categoryName; }

    public String getCategoryType() { return categoryType; }

    public String getOwnerEmail() { return ownerEmail; }
}

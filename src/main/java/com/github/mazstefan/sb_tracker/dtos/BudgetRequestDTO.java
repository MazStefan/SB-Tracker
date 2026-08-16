package com.github.mazstefan.sb_tracker.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public class BudgetRequestDTO {

    @NotNull(message = "Monthly limit is required")
    @Positive(message = "Monthly limit must be greater than zero")
    private BigDecimal monthlyLimit;

    @NotNull(message = "Month and year are required")
    private LocalDate monthYear;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    public BigDecimal getMonthlyLimit() { return monthlyLimit; }
    public void setMonthlyLimit(BigDecimal monthlyLimit) { this.monthlyLimit = monthlyLimit; }

    public LocalDate getMonthYear() { return monthYear; }
    public void setMonthYear(LocalDate monthYear) { this.monthYear = monthYear; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}

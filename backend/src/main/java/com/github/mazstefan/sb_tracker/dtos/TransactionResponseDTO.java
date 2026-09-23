package com.github.mazstefan.sb_tracker.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponseDTO {
    
    private Long id;
    private BigDecimal amount;
    private String description;
    private LocalDateTime date;
    private String categoryName;
    private String categoryType;
    private String ownerEmail;

    // USER CONSTRUCOTR
    public TransactionResponseDTO(Long id, BigDecimal amount, String description, LocalDateTime date, String categoryName, String categoryType) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
    }

    // ADMIN CONSTRUCTOR
    public TransactionResponseDTO(Long id, BigDecimal amount, String description, LocalDateTime date, String categoryName, String categoryType, String ownerEmail) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.categoryName = categoryName;
        this.categoryType = categoryType;
        this.ownerEmail = ownerEmail;
    }

    public Long getId() { return id; }

    public BigDecimal getAmount() { return amount; }

    public String getDescription() { return description; }

    public LocalDateTime getDate() { return date; }

    public String getCategoryName() { return categoryName; }

    public String getCategoryType() { return categoryType; }

    public String getOwnerEmail() { return ownerEmail; }
}

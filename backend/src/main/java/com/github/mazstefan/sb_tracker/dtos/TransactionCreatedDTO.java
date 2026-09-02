package com.github.mazstefan.sb_tracker.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionCreatedDTO(
    Long id,
    BigDecimal amount,
    String description, 
    LocalDateTime date, 
    String categoryName,
    String categoryType,
    Boolean overSpend
) 
{}

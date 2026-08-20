package com.github.mazstefan.sb_tracker.dtos;

import java.math.BigDecimal;

public record CategorySpendDTO(String categoryName, BigDecimal totalSpent) {
    
}

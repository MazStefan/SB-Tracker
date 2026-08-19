package com.github.mazstefan.sb_tracker.controllers;

import com.github.mazstefan.sb_tracker.dtos.TransactionRequestDTO;
import com.github.mazstefan.sb_tracker.dtos.TransactionResponseDTO;
import com.github.mazstefan.sb_tracker.services.TransactionService;
import com.github.mazstefan.sb_tracker.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            Authentication authentication,
            @Valid @RequestBody TransactionRequestDTO requestDTO) {

        Long currentUserId = extractUserId(authentication);

        TransactionResponseDTO createdTransaction = transactionService.createTransaction(requestDTO, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getMyTransactions(Authentication authentication) {
        
        Long currentUserId = extractUserId(authentication);

        List<TransactionResponseDTO> transactions = transactionService.getUserTransactions(currentUserId);

        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getMyTransaction(
            Authentication authentication,
            @PathVariable long id) {

        Long currentUserId = extractUserId(authentication);

        TransactionResponseDTO transaction = transactionService.getTransactionById(id, currentUserId);

        return ResponseEntity.ok(transaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequestDTO requestDTO) {

        Long currentUserId = extractUserId(authentication);

        TransactionResponseDTO updatedTransaction = transactionService.updateTransaction(requestDTO, id, currentUserId);

        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            Authentication authentication,
            @PathVariable Long id) {
        
        Long currentUserId = extractUserId(authentication);

        transactionService.deleteTransaction(id, currentUserId);

        return ResponseEntity.noContent().build();
    }

    private Long extractUserId(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }
}

package com.github.mazstefan.sb_tracker.controllers;

import com.github.mazstefan.sb_tracker.dtos.TransactionRequestDTO;
import com.github.mazstefan.sb_tracker.dtos.TransactionResponseDTO;
import com.github.mazstefan.sb_tracker.services.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
            @Valid @RequestBody TransactionRequestDTO requestDTO) {
        //TODO: Replace with real JWT token generation in week 2
        Long currentUserId = 1L;

        TransactionResponseDTO createdTransaction = transactionService.createTransaction(requestDTO, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getMyTransactions() {
        //TODO: Replace with real JWT token generation in week 2
        Long currentUserId = 1L;

        List<TransactionResponseDTO> transactions = transactionService.getUserTransactions(currentUserId);

        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getMyTransaction(@PathVariable long id) {
        //TODO: Replace with real JWT token generation in week 2
        Long currentUserId = 1L;

        TransactionResponseDTO transaction = transactionService.getTransactionById(id, currentUserId);

        return ResponseEntity.ok(transaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequestDTO requestDTO) {
        //TODO: Replace with real JWT token generation in week 2
        Long currentUserId = 1L;

        TransactionResponseDTO updatedTransaction = transactionService.updateTransaction(requestDTO, id, currentUserId);

        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        //TODO: Replace with real JWT token generation in week 2
        Long currentUserId = 1L;

        transactionService.deleteTransaction(id, currentUserId);

        return ResponseEntity.noContent().build();
    }
}

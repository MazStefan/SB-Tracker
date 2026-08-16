package com.github.mazstefan.sb_tracker.services;

import com.github.mazstefan.sb_tracker.dtos.TransactionRequestDTO;
import com.github.mazstefan.sb_tracker.dtos.TransactionResponseDTO;
import com.github.mazstefan.sb_tracker.entities.Transaction;
import com.github.mazstefan.sb_tracker.entities.Category;
import com.github.mazstefan.sb_tracker.entities.User;
import com.github.mazstefan.sb_tracker.repositories.TransactionRepository;
import com.github.mazstefan.sb_tracker.repositories.CategoryRepository;
import com.github.mazstefan.sb_tracker.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository) {
        
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    public TransactionResponseDTO createTransaction(TransactionRequestDTO requestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Category category = categoryRepository.findByIdAndUserId(requestDTO.getCategoryId(), userId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        Transaction transaction = new Transaction();
        transaction.setAmount(requestDTO.getAmount());
        transaction.setDescription(requestDTO.getDescription());
        transaction.setDate(requestDTO.getDate());
        transaction.setUser(user);
        transaction.setCategory(category);

        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToResponseDTO(savedTransaction);
    }

    public List<TransactionResponseDTO> getUserTransactions(Long userId) {
        List<Transaction> transactions = transactionRepository.findAllByUserId(userId);

        return transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public TransactionResponseDTO getTransactionById(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        return mapToResponseDTO(transaction);
    }

    public TransactionResponseDTO updateTransaction(TransactionRequestDTO requestDTO, Long transactionId, Long userId) {
        Transaction existingTransaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        Category category = categoryRepository.findByIdAndUserId(requestDTO.getCategoryId(), userId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        existingTransaction.setAmount(requestDTO.getAmount());
        existingTransaction.setDescription(requestDTO.getDescription());
        existingTransaction.setDate(requestDTO.getDate());
        existingTransaction.setCategory(category);

        Transaction updatedTransaction = transactionRepository.save(existingTransaction);
        
        return mapToResponseDTO(updatedTransaction);
    }

    public void deleteTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
            
        transactionRepository.delete(transaction);
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getId(), 
                transaction.getAmount(), 
                transaction.getDescription(),
                transaction.getDate(),
                transaction.getCategory().getName()
        );
    }
}

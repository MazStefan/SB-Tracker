package com.github.mazstefan.sb_tracker.services;

import com.github.mazstefan.sb_tracker.dtos.CategorySpendDTO;
import com.github.mazstefan.sb_tracker.dtos.TransactionRequestDTO;
import com.github.mazstefan.sb_tracker.dtos.TransactionResponseDTO;
import com.github.mazstefan.sb_tracker.dtos.TransactionCreatedDTO;
import com.github.mazstefan.sb_tracker.entities.Transaction;
import com.github.mazstefan.sb_tracker.entities.Category;
import com.github.mazstefan.sb_tracker.entities.User;
import com.github.mazstefan.sb_tracker.entities.enums.Role;
import com.github.mazstefan.sb_tracker.entities.Budget;
import com.github.mazstefan.sb_tracker.repositories.TransactionRepository;
import com.github.mazstefan.sb_tracker.repositories.BudgetRepository;
import com.github.mazstefan.sb_tracker.repositories.CategoryRepository;
import com.github.mazstefan.sb_tracker.repositories.UserRepository;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public TransactionService(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            BudgetRepository budgetRepository,
            SimpMessagingTemplate messagingTemplate) {
        
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public TransactionCreatedDTO createTransaction(TransactionRequestDTO requestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long categoryId = requestDTO.getCategoryId();
        int targetMonth = requestDTO.getDate().getMonthValue();
        int targetYear = requestDTO.getDate().getYear();

        Category category = findCategoryForUserContext(categoryId, user);

        Double budgetLimit = findLimitForUserContext(categoryId, targetMonth, targetYear, user);

        Double transactionSum = findTransactionSumForUserContext(categoryId, targetMonth, targetYear, user);

        Boolean overSpend = false;

        if (budgetLimit != null && (transactionSum + requestDTO.getAmount().doubleValue() > budgetLimit)) {
                overSpend = true;
        }
        
        Transaction transaction = new Transaction();
        transaction.setAmount(requestDTO.getAmount());
        transaction.setDescription(requestDTO.getDescription());
        transaction.setDate(requestDTO.getDate());
        transaction.setUser(user);
        transaction.setCategory(category);

        Transaction savedTransaction = transactionRepository.save(transaction);

        boolean isAdmin = user.getRole().name().equals("ADMIN");

        boolean includeOwnerInfo = isAdmin || user.getGroup() != null;

        if (user.getGroup() != null) {
            String destination = "/topic/group/" + user.getGroup().getId();
            
            Map<String, String> payload = Map.of("action", "REFRESH_TRANSACTIONS");
            messagingTemplate.convertAndSend(destination, payload);
        }

        return mapToCreatedDTO(savedTransaction, overSpend, includeOwnerInfo);
    }

    public List<TransactionResponseDTO> getUserTransactions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Transaction> transactions;

        boolean isAdmin = user.getRole().name().equals("ADMIN");

        if(isAdmin) {

                transactions = transactionRepository.findAll();

        } else {

                transactions = findAllTransactionForUserContext(user);

        }

        boolean includeOwnerInfo = isAdmin || user.getGroup() != null;

        return transactions.stream()
                .map(transaction -> mapToResponseDTO(transaction, includeOwnerInfo))
                .collect(Collectors.toList());
    }

    public TransactionResponseDTO getTransactionById(Long transactionId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction transaction = findTransactionForUserContext(transactionId, user);
        
        boolean isAdmin = user.getRole().name().equals("ADMIN");

        boolean includeOwnerInfo = isAdmin || user.getGroup() != null;

        return mapToResponseDTO(transaction, includeOwnerInfo);
    }

    public TransactionCreatedDTO updateTransaction(TransactionRequestDTO requestDTO, Long transactionId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Long categoryId = requestDTO.getCategoryId();

        Transaction existingTransaction = findTransactionForUserContext(transactionId, user);

        Category category = findCategoryForUserContext(categoryId, user);

        int targetMonth = requestDTO.getDate().getMonthValue();
        int targetYear = requestDTO.getDate().getYear();
        
        Double budgetLimit = findLimitForUserContext(categoryId, targetMonth, targetYear, user);

        Double transactionSum = findTransactionSumForUserContext(categoryId, targetMonth, targetYear, user);

        boolean isSameCategory = existingTransaction.getCategory().getId().equals(category.getId());
        boolean isSameMonth = existingTransaction.getDate().getMonthValue() == targetMonth;
        boolean isSameYear = existingTransaction.getDate().getYear() == targetYear;

        double updatedTotal = transactionSum;

        if (isSameCategory && isSameMonth && isSameYear) {
                updatedTotal -= existingTransaction.getAmount().doubleValue();
        } 

        updatedTotal += requestDTO.getAmount().doubleValue();
        
        Boolean overSpend = false;

        if (budgetLimit != null && updatedTotal > budgetLimit) {
                overSpend = true;
        }

        existingTransaction.setAmount(requestDTO.getAmount());
        existingTransaction.setDescription(requestDTO.getDescription());
        existingTransaction.setDate(requestDTO.getDate());
        existingTransaction.setCategory(category);

        Transaction updatedTransaction = transactionRepository.save(existingTransaction);

        boolean isAdmin = user.getRole().name().equals("ADMIN");

        boolean includeOwnerInfo = isAdmin || user.getGroup() != null;

        if (user.getGroup() != null) {
            String destination = "/topic/group/" + user.getGroup().getId();
            
            Map<String, String> payload = Map.of("action", "REFRESH_TRANSACTIONS");
            messagingTemplate.convertAndSend(destination, payload);
        }
        
        return mapToCreatedDTO(updatedTransaction, overSpend, includeOwnerInfo);
    }

    public void deleteTransaction(Long transactionId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction transaction = findTransactionForUserContext(transactionId, user);
            
        transactionRepository.delete(transaction);

        if (user.getGroup() != null) {
            String destination = "/topic/group/" + user.getGroup().getId();
            
            Map<String, String> payload = Map.of("action", "REFRESH_TRANSACTIONS");
            messagingTemplate.convertAndSend(destination, payload);
        }
    }

    public List<CategorySpendDTO> generateMonthlyReport(Long userId, int month, int year) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean includeOwnerInfo = isAdmin || user.getGroup() != null;

        List<CategorySpendDTO> spent;
        List<Budget> budgets;

        if (isAdmin) {
                spent = transactionRepository.getAllMonthlySpendReport(month, year); 
                budgets = budgetRepository.findAll(); 
        } else {
                spent = findAllSpendForUserContext(user, month, year);
                budgets = findAllBudgetForUserContext(user);
        }

        return spent.stream().map(spend -> {
                Budget matchingBudget = budgets.stream()
                        .filter(b -> 
                        b.getCategory().getName().equals(spend.categoryName()) &&
                        b.getUser().getEmail().equals(spend.ownerEmail()) 
                        )
                        .findFirst()
                        .orElse(null);
                        
                BigDecimal limit = matchingBudget != null 
                        ? matchingBudget.getMonthlyLimit() 
                        : BigDecimal.ZERO;

                String finalOwnerEmail = includeOwnerInfo ? spend.ownerEmail() : null;

                return spend.withLimitOwner(limit, finalOwnerEmail);
        }).toList();
    }

    private Transaction findTransactionForUserContext(Long transactionId, User user) {
        if (user.getGroup() != null)
                return transactionRepository.findByIdAndUserGroupId(transactionId, user.getGroup().getId())
                                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        else
                return transactionRepository.findByIdAndUserId(transactionId, user.getId())
                                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    private List<Transaction> findAllTransactionForUserContext(User user) {
        if (user.getGroup() != null)
                return transactionRepository.findAllByUserGroupId(user.getGroup().getId());
        else
                return transactionRepository.findAllByUserId(user.getId());
    }

    private List<CategorySpendDTO> findAllSpendForUserContext(User user, int month, int year) {
        if (user.getGroup() != null)
                return transactionRepository.getGroupMonthlySpendReport(user.getGroup().getId(), month, year);
        else
                return transactionRepository.getMonthlySpendReport(user.getId(), month, year);
    }

    private List<Budget> findAllBudgetForUserContext(User user) {
        if (user.getGroup() != null)
                return budgetRepository.findAllByUserGroupId(user.getGroup().getId());
        else
                return budgetRepository.findAllByUserId(user.getId());
    }

    private Category findCategoryForUserContext(Long categoryId, User user) {
        if (user.getGroup() != null) 
            return categoryRepository.findByIdAndUserGroupId(categoryId, user.getGroup().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found in group"));
        else
                return categoryRepository.findByIdAndUserId(categoryId, user.getId())
                        .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    private Double findLimitForUserContext(Long categoryId, int month, int year, User user) {
        if (user.getGroup() != null)
                return budgetRepository.findGroupLimitByYearAndMonth(user.getGroup().getId(), categoryId, month, year)
                                        .orElse(null);
        else
                return budgetRepository.findLimitByYearAndMonth(user.getId(), categoryId, month, year)
                                        .orElse(null);
    }

    private Double findTransactionSumForUserContext(Long categoryId, int month, int year, User user) {
        if (user.getGroup() != null)
                return transactionRepository.sumGroupTransactionsByCategoryAndMonth(user.getGroup().getId(), categoryId, month, year)
                                        .orElse(0.0);
        else
                return transactionRepository.sumTransactionsByCategoryAndMonth(user.getId(), categoryId, month, year)
                                        .orElse(0.0);
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction, boolean includeOwnerInfo) {
        String ownerEmail = includeOwnerInfo ? transaction.getUser().getEmail() : null;

        return new TransactionResponseDTO(
                transaction.getId(), 
                transaction.getAmount(), 
                transaction.getDescription(),
                transaction.getDate(),
                transaction.getCategory().getName(),
                transaction.getCategory().getType().name(),
                ownerEmail
        );
    }

    private TransactionCreatedDTO mapToCreatedDTO(Transaction transaction, Boolean overSpend, boolean includeOwnerInfo) {
        String ownerEmail = includeOwnerInfo ? transaction.getUser().getEmail() : null;

        return new TransactionCreatedDTO(
                transaction.getId(), 
                transaction.getAmount(), 
                transaction.getDescription(),
                transaction.getDate(),
                transaction.getCategory().getName(),
                transaction.getCategory().getType().name(),
                overSpend,
                ownerEmail
        );
    }
}

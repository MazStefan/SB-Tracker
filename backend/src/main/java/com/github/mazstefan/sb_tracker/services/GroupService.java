package com.github.mazstefan.sb_tracker.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.mazstefan.sb_tracker.dtos.GroupRequestDTO;
import com.github.mazstefan.sb_tracker.dtos.GroupResponseDTO;
import com.github.mazstefan.sb_tracker.entities.Category;
import com.github.mazstefan.sb_tracker.entities.Transaction;
import com.github.mazstefan.sb_tracker.entities.User;
import com.github.mazstefan.sb_tracker.entities.UserGroup;
import com.github.mazstefan.sb_tracker.repositories.CategoryRepository;
import com.github.mazstefan.sb_tracker.repositories.TransactionRepository;
import com.github.mazstefan.sb_tracker.repositories.UserGroupRepository;
import com.github.mazstefan.sb_tracker.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor
public class GroupService {
    private final UserGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    @Transactional 
    public GroupResponseDTO createGroup(GroupRequestDTO request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getGroup() != null) throw new RuntimeException("User already in a group");

        UserGroup group = new UserGroup();
        group.setName(request.name());
        group.setInviteCode(generateUniqueInviteCode());
        group.addMember(user);

        groupRepository.save(group);
        userRepository.save(user);

        return mapToResponseDTO(group);
    }

    @Transactional 
    public GroupResponseDTO joinGroup(String inviteCode, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        UserGroup group = groupRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new RuntimeException("Invalid invite code"));
        
        group.addMember(user);

        userRepository.save(user);

        return mapToResponseDTO(group);
    }

    @Transactional 
    public void leaveGroup(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        UserGroup userGroup = user.getGroup();

        if (userGroup == null)
            throw new RuntimeException("User is not in a group.");

        List<Transaction> userTransactions = transactionRepository.findAllByUserId(userId);

        for (Transaction t : userTransactions) {
            Category sharedCategory = t.getCategory();

            if (!sharedCategory.getUser().getId().equals(userId)) {
                Category personalCategory = categoryRepository
                        .findByUserIdAndNameAndType(userId, sharedCategory.getName(), sharedCategory.getType())
                        .orElseGet(() -> {
                            Category newCategory = new Category();
                            newCategory.setName(sharedCategory.getName());
                            newCategory.setType(sharedCategory.getType());
                            newCategory.setUser(user);
                            return categoryRepository.save(newCategory);
                        });

                t.setCategory(personalCategory);
            }
        }

        transactionRepository.saveAll(userTransactions);

        userGroup.removeMember(user);

        userRepository.save(user);

        if(userGroup.getMembers().isEmpty())
            groupRepository.delete(userGroup);
    }

    private String generateUniqueInviteCode() {
        return UUID.randomUUID().toString().substring(0,8).toUpperCase();
    }

    private GroupResponseDTO mapToResponseDTO(UserGroup group) {
        List<String> memberNames = group.getMembers().stream()
            .map(user -> user.getEmail())
            .toList();

        return new GroupResponseDTO(
            group.getId(),
            group.getName(),
            group.getInviteCode(),
            memberNames
        );
    }
}

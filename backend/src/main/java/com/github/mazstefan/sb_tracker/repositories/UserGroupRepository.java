package com.github.mazstefan.sb_tracker.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.mazstefan.sb_tracker.entities.UserGroup;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long>{

    Optional<UserGroup> findByInviteCode(String inviteCode);

    boolean existsByInviteCode(String inviteCode);
    
}

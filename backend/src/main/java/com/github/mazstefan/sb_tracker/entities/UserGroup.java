package com.github.mazstefan.sb_tracker.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table(name = "user_groups")
@Getter 
@Setter 
public class UserGroup {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @Column(nullable = false, unique = true)
    private String name;

    @Column(unique = true, nullable = false, updatable = false)
    private String inviteCode;

    @OneToMany(mappedBy = "group")
    private List<User> members = new ArrayList<>();

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getInviteCode() { return inviteCode; }
    public void setInviteCode(String inviteCode) { this.inviteCode = inviteCode; }

    public void addMember(User user) {
        this.members.add(user);
        user.setGroup(this);
    }

    public void removeMember(User user) {
        this.members.remove(user);
        user.setGroup(null);
    }
}

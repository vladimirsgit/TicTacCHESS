package com.tictacchess.backend.dto;

import com.tictacchess.backend.model.UserRole;
import java.sql.Timestamp;

public class UserDTO {
    private String username;
    private UserRole role;
    private Timestamp createdAt;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}

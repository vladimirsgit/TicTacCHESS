package com.tictacchess.model;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String username;
    private String last_name;
    private String first_name;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.common;

    private String email;
    private Timestamp created_at = new Timestamp(System.currentTimeMillis());
    private String confirmation_code;
    private Boolean confirmedEmail = false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getConfirmation_code() {
        return confirmation_code;
    }

    public void setConfirmation_code(String confirmation_code) {
        this.confirmation_code = confirmation_code;
    }

    public Boolean getConfirmedEmail() {
        return confirmedEmail;
    }

    public void setConfirmedEmail(Boolean confirmed_email) {
        this.confirmedEmail = confirmed_email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp timestamp) {
        this.created_at = timestamp;
    }
}


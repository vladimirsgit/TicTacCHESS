package com.tictacchess.repository;

import com.tictacchess.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByUsername(String username);
    User findUserByUsername(String username);
    ArrayList<User> findUsersByConfirmedEmailFalse();
}

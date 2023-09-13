package com.tictacchess.repository;

import com.tictacchess.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByUsername(String username);
    User findUserByUsername(String username);
    ArrayList<User> findUsersByConfirmedEmailFalse();
}

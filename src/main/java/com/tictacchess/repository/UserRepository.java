package com.tictacchess.repository;

import com.tictacchess.model.Friendship;
import com.tictacchess.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByUsername(String username);
    User findUserByUsername(String username);
    ArrayList<User> findUsersByConfirmedEmailFalse();

//    @Query("SELECT friendship FROM Friendship friendship WHERE (friendship.requesterId =:userId OR friendship.recipientId =:userId) " +
//            "AND friendship.pending = false AND friendship.declined = false")
//    ArrayList<Friendship> exists(Integer userId);
}

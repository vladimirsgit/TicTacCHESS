package com.tictacchess.repository;

import com.tictacchess.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Friendship.FriendshipId> {
    //used to make sure that there isnt a relationship between them
    boolean existsFriendshipByRecipientIdAndRequesterId(Integer recipientId, Integer RequesterId);
    //used to see if theres a friend request pending
    boolean existsFriendshipByRequesterIdAndRecipientIdAndPendingIsTrue(Integer requesterId, Integer recipientId);
    //used to see if they are already friends - pending false and declined false means friendship was accepted
    boolean existsFriendshipByRequesterIdAndRecipientIdAndPendingIsFalseAndDeclinedIsFalse(Integer requesterId, Integer recipientId);
    //checking to see if the friend request was declined
    boolean existsFriendshipByRequesterIdAndRecipientIdAndDeclinedIsTrue(Integer requesterId, Integer recipientId);
    @Modifying
    @Query("DELETE FROM Friendship WHERE requesterId = :requesterId AND recipientId = :recipientId")
    void deleteFriendshipByRequesterIdAndRecipientId(@Param("requesterId") Integer requesterId, @Param("recipientId") Integer recipientId);
    boolean existsFriendshipByRequesterIdAndRecipientIdAndPendingIsFalseAndDeclinedTrue(Integer requesterId, Integer recipientId);
    ArrayList<Friendship> findFriendshipsByDeclinedTrue();

    @Query("SELECT f FROM Friendship f WHERE (f.requesterId = :userId OR f.recipientId = :userId) AND f.pending = false AND f.declined = FALSE")
    ArrayList<Friendship> findFriendship(@Param("userId") Integer userId);

    //@Query("SELECT COUNT (f) > 0 FROM Friendship f WHERE (f.requesterId = :friend AND f.recipientId = :friendOf) OR (f.requesterId = :friendOf AND f.recipientId = :friend)")
    //boolean existsFriendshipToDelete(@Param("friend") Integer friendId, @Param("friendOf") Integer friendOfId);

    @Query("SELECT f FROM Friendship f WHERE (f.requesterId = :friend AND f.recipientId = :friendOf) OR (f.requesterId = :friendOf AND f.recipientId = :friend)")
    Friendship friendshipToDelete(@Param("friend") Integer friendId, @Param("friendOf") Integer friendOfId);
}

package com.tictacchess.repository;

import com.tictacchess.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}

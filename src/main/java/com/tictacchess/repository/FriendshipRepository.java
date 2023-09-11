package com.tictacchess.repository;

import com.tictacchess.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Friendship.FriendshipId> {
    boolean existsFriendshipByRecipientIdAndRequesterId(Integer recipientId, Integer RequesterId);
}

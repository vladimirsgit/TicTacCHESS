package com.tictacchess.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tictacchess.backend.model.Friendship;

import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Friendship.FriendshipId> {
    boolean existsFriendshipByRecipientIdAndRequesterId(Integer recipientId, Integer RequesterId);
}

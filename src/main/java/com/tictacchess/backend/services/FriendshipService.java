package com.tictacchess.backend.services;

import com.tictacchess.backend.exceptions.DatabaseException;
import com.tictacchess.backend.exceptions.FriendshipAlreadyExistsException;
import com.tictacchess.backend.exceptions.UserNotFoundException;
import com.tictacchess.backend.model.Friendship;
import com.tictacchess.backend.model.User;
import com.tictacchess.backend.repository.FriendshipRepository;
import com.tictacchess.backend.repository.UserRepository;
import org.springframework.stereotype.Service;


@Service
public class FriendshipService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    public FriendshipService(UserRepository userRepository, FriendshipRepository friendshipRepository){
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    public void addFriend(String requesterUsername, String recipientUsername){
        User requester = userRepository.findUserByUsername(requesterUsername);
        User recipient = userRepository.findUserByUsername(recipientUsername);

        if(requester == null || recipient == null || requesterUsername.equals(recipientUsername)){
            throw new UserNotFoundException("Something is wrong with the username/usernames");
        }

        if(checkIfFriendshipExists(requester.getId(), recipient.getId())){
            throw new FriendshipAlreadyExistsException("Friendship or friend request already exists!");
        }
        try {
            friendshipRepository.save(new Friendship(requester.getId(), recipient.getId()));
        } catch (Exception e){
            throw new DatabaseException("A database error occurred while trying to save the friendship request.");
        }
    }

    public boolean checkIfFriendshipExists(Integer requesterId, Integer recipientId){
        return friendshipRepository.existsFriendshipByRecipientIdAndRequesterId(recipientId, requesterId)
                || friendshipRepository.existsFriendshipByRecipientIdAndRequesterId(requesterId, recipientId);
    }

}

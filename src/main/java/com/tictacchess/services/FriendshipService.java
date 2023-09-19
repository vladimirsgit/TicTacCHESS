package com.tictacchess.services;

import com.tictacchess.dto.UserDTO;
import com.tictacchess.exceptions.AddFriendException;
import com.tictacchess.exceptions.DatabaseException;
import com.tictacchess.exceptions.FriendshipException;
import com.tictacchess.exceptions.UserNotFoundException;
import com.tictacchess.model.Friendship;
import com.tictacchess.model.User;
import com.tictacchess.repository.FriendshipRepository;
import com.tictacchess.repository.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Objects;


@Service
public class FriendshipService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    public static final String ACTION_ACCEPT = "accept";
    public static final String ACTION_DECLINE = "decline";

    public FriendshipService(UserRepository userRepository, FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    //method for adding a friend
    public ResponseEntity<String> addFriend(String recipientUsername, HttpSession httpSession) {
        validateFriendshipRequestData(recipientUsername, httpSession);

        User requester = userRepository.findUserByUsername(httpSession.getAttribute("username").toString());
        User recipient = userRepository.findUserByUsername(recipientUsername);

        try {
            friendshipRepository.save(new Friendship(requester.getId(), recipient.getId()));
        } catch (Exception e) {
            throw new DatabaseException("A database error occurred while trying to save the friendship request.");
        }
        return new ResponseEntity<>("Friend request sent!", HttpStatus.OK);
    }
    @Transactional
    public ResponseEntity<String> cancelRequest(String recipientUsername, HttpSession httpSession){
        validateFriendRequestCancelData(recipientUsername, httpSession);

        User requester = userRepository.findUserByUsername(httpSession.getAttribute("username").toString());
        User recipient = userRepository.findUserByUsername(recipientUsername);

        try {
            friendshipRepository.deleteFriendshipByRequesterIdAndRecipientId(requester.getId(), recipient.getId());
        } catch (Exception e){
            throw new DatabaseException("A database error occurred while trying to cancel the request.");
        }
        return new ResponseEntity<>("Friend request canceled!", HttpStatus.OK);
    }
    public ResponseEntity<String> acceptFriendship(String requesterUsername, HttpSession httpSession) {
        checkFriendshipDataAndSetNecessaryActions(requesterUsername, httpSession, ACTION_ACCEPT);
        return new ResponseEntity<>("Friend request accepted!", HttpStatus.ACCEPTED);
    }

    public ResponseEntity<String> declineFriendship(String requesterUsername, HttpSession httpSession) {
        checkFriendshipDataAndSetNecessaryActions(requesterUsername, httpSession, ACTION_DECLINE);
        return new ResponseEntity<>("Friend request declined!", HttpStatus.OK);

    }
    public ResponseEntity<String> deleteFriend(String friendUsername, HttpSession httpSession){
        Friendship friendship = validateFriendDeletionData(friendUsername, httpSession);
        try {
            friendshipRepository.delete(friendship);
        } catch (Exception e){
            throw new DatabaseException("An error occurred when trying to delete that friendship");
        }
        return new ResponseEntity<>("Friend deleted!", HttpStatus.OK);
    }
    public Friendship validateFriendDeletionData(String friendUsername, HttpSession httpSession){
        if(httpSession.getAttribute("username") == null){
            throw new UserNotFoundException("Please log in!");
        }
        User friendOf = userRepository.findUserByUsername(httpSession.getAttribute("username").toString());
        User friend = userRepository.findUserByUsername(friendUsername);

        if(friendOf == null || friend == null){
            throw new UserNotFoundException("Something is wrong with the username/usernames");
        }
        Friendship friendship = friendshipRepository.friendshipToDelete(friend.getId(), friendOf.getId());

        if(friendship == null){
            throw new FriendshipException("Friendship not found.");
        }
        return friendship;
    }
    public void validateFriendRequestCancelData(String recipientUsername, HttpSession httpSession){
        if(httpSession.getAttribute("username") == null){
            throw new UserNotFoundException("Please log in before trying this!");
        }
        String requesterUsername = httpSession.getAttribute("username").toString();

        User requester = userRepository.findUserByUsername(requesterUsername);
        User recipient = userRepository.findUserByUsername(recipientUsername);

        if(requester == null || recipient == null || requesterUsername.equals(recipientUsername)){
            throw new UserNotFoundException("Something is wrong with the username/usernames");
        }

        if(!friendshipRepository.existsFriendshipByRequesterIdAndRecipientIdAndPendingIsTrue(requester.getId(), recipient.getId())){
            throw new FriendshipException("No friend request found!");
        }

    }

    public void validateFriendshipRequestData(String recipientUsername, HttpSession httpSession) {
        //checking to see if the user is logged in
        if (httpSession.getAttribute("username") == null) {
            throw new UserNotFoundException("Please log in before sending friend requests!");
        }
        String requesterUsername = httpSession.getAttribute("username").toString();

        User requester = userRepository.findUserByUsername(requesterUsername);
        User recipient = userRepository.findUserByUsername(recipientUsername);
        //we make sure that the users were found in the database
        if (requester == null || recipient == null || requesterUsername.equals(recipientUsername)) {
            throw new UserNotFoundException("Something is wrong with the username/usernames");
        }

        if(friendshipRepository.existsFriendshipByRequesterIdAndRecipientIdAndDeclinedIsTrue(requester.getId(), recipient.getId())){
            throw new FriendshipException("Your friend request was already declined. :(");
        }
        //making sure that the friendship doesnt already exist
        if (checkIfFriendshipExists(requester.getId(), recipient.getId())) {
            throw new FriendshipException("Friendship or friend request already exists!");
        }
    }


    public void checkFriendshipDataAndSetNecessaryActions(String requesterUsername, HttpSession httpSession, String action){
        if (httpSession.getAttribute("username") == null) {
            throw new UserNotFoundException("Please log in!");
        }
        User requester = userRepository.findUserByUsername(requesterUsername);
        User recipient = userRepository.findUserByUsername(httpSession.getAttribute("username").toString());

        validateFriendshipAcceptanceOrDeclinationData(requester, recipient, action);

        Friendship friendship = new Friendship(requester.getId(), recipient.getId());
        friendship.setPending(false);
        if(action.equals(ACTION_DECLINE)){
            friendship.setDeclined(true);
            friendship.setDeclined_at(new Timestamp(System.currentTimeMillis()));
        }
        try {
            friendshipRepository.save(friendship);
        } catch (Exception e) {
            throw new DatabaseException("Database error while saving user");
        }
    }

    public void validateFriendshipAcceptanceOrDeclinationData(User requester, User recipient, String action) {
        if (requester == null) {
            throw new UserNotFoundException("User does not exist!");
        }
        //then we make sure that the request exists
        if (!friendshipRepository.existsFriendshipByRequesterIdAndRecipientIdAndPendingIsTrue(requester.getId(), recipient.getId())) {
            String errMessage = "You do not have a request to decline";
            if(action.equals(ACTION_ACCEPT)){
                errMessage = "No friend request available";
            }
            throw new AddFriendException(errMessage);
        }
        //checking if the request hasnt already been declined/accepted
        if (friendshipRepository.existsFriendshipByRequesterIdAndRecipientIdAndDeclinedIsTrue(requester.getId(), recipient.getId())) {
            throw new AddFriendException("Friendship request already declined!");
        }
        if (friendshipRepository.existsFriendshipByRequesterIdAndRecipientIdAndPendingIsFalseAndDeclinedIsFalse(requester.getId(), recipient.getId())) {
            throw new AddFriendException("You are already friends!");
        }
    }

    //we make sure that there isnt a pending or accepted friendship
    public boolean checkIfFriendshipExists(Integer requesterId, Integer recipientId){
        return friendshipRepository.existsFriendshipByRecipientIdAndRequesterId(recipientId, requesterId)
                || friendshipRepository.existsFriendshipByRecipientIdAndRequesterId(requesterId, recipientId);
    }

    public String showListOfFriends(HttpSession httpSession, Model model){
        if(httpSession.getAttribute("username") == null){
            return "redirect:/login";
        }
        ArrayList<UserDTO> friendsList = createListOfFriends(httpSession);
        model.addAttribute("friendsList", friendsList);

        return "friends";
    }
    public ArrayList<UserDTO> createListOfFriends(HttpSession httpSession){
        User user = userRepository.findUserByUsername(httpSession.getAttribute("username").toString());
        if(user == null){
            throw new UserNotFoundException("User not found");
        }

        ArrayList<Friendship> friendships = friendshipRepository.findFriendship(user.getId());
        ArrayList<UserDTO> friends = new ArrayList<>();
        Integer userId = user.getId();

        for(Friendship friendship:
        friendships){
            if(Objects.equals(friendship.getRequesterId(), userId)){
                User friend = userRepository.findUserById(friendship.getRecipientId());
                UserDTO userDTO = createUserDTOForFriendsList(friend);
                friends.add(userDTO);
            } else if(Objects.equals(friendship.getRecipientId(), userId)){
                User friend = userRepository.findUserById(friendship.getRequesterId());
                UserDTO userDTO = createUserDTOForFriendsList(friend);
                friends.add(userDTO);
            }
        }
        return friends;
    }

    public UserDTO createUserDTOForFriendsList(User friend){
        UserDTO userDTO = new UserDTO();
        userDTO.setCreatedAt(friend.getCreated_at());
        userDTO.setRole(friend.getRole());
        userDTO.setUsername(friend.getUsername());

        return userDTO;
    }

}

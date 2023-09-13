package com.tictacchess.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tictacchess.dto.UserDTO;
import com.tictacchess.exceptions.AuthDataInvalid;
import com.tictacchess.exceptions.DatabaseException;
import com.tictacchess.exceptions.UserAlreadyExistsException;
import com.tictacchess.exceptions.UserNotFoundException;
import com.tictacchess.model.User;
import com.tictacchess.repository.FriendshipRepository;
import com.tictacchess.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Objects;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final FriendshipRepository friendshipRepository;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.friendshipRepository = friendshipRepository;
    }
    //confirming Emails
    public ResponseEntity<String> confirmEmail(String username, String token){
        User user = userRepository.findUserByUsername(username);
        if(user == null) throw new UserNotFoundException("No user found.");
        if(user.getConfirmedEmail()) throw new UserAlreadyExistsException("Email is already confirmed!");

        if(!Objects.equals(user.getConfirmation_code(), token)){
            throw new AuthDataInvalid("Invalid data");
        }
        //if the user exists or is it already confirmed or the token is wrong, it will throw errors
        //if not, we can save in the DB that the user has confirmed their email
        user.setConfirmedEmail(true);
        try {
            userRepository.save(user);
        } catch (Exception e){
            throw new DatabaseException("An error occurred while saving the user");
        }
        return new ResponseEntity<>("Email confirmed!", HttpStatus.ACCEPTED);
    }
    //function to see if the profile page to show is of another user, or if its the logged in user's profile
    public String whatProfileDataToShow(String username, HttpSession httpSession, Model model){
        if(httpSession.getAttribute("username") == null){
            return "login";
        }
        if(httpSession.getAttribute("username").equals(username)){
            return "selfProfile";
        }
        return showOtherProfile(username, httpSession, model);
    }
    //creating user data to show the profile of another user
    public String showOtherProfile(String username, HttpSession httpSession, Model model){
        //user to show is the user that must be shown, and user to see is the one that wants to see the profile
        User userToShow = userRepository.findUserByUsername(username);
        User userToSee = userRepository.findUserByUsername(httpSession.getAttribute("username").toString());
        //if the user doesnt exist, we redirect to 404 not found
        if(userToShow == null || !userToShow.getConfirmedEmail()){
            return "redirect:/404";
        }

        //here we use a data transfer object to create the user's profile
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(userToShow.getUsername());
        userDTO.setRole(userToShow.getRole());
        userDTO.setCreatedAt(userToShow.getCreated_at());

        //we want to set the status of their friendship
        setFriendshipModelData(userDTO, userToSee, userToShow, model);

        return "otherProfile";
    }
    //method for updating profile data. the user can update their name and last name
    public ResponseEntity<String> updateProfile(ObjectNode requestBodyJson){
        //making sure the user exists
        if(!userRepository.existsUserByUsername(requestBodyJson.get("username").asText())){
            throw new UserNotFoundException("User not found!");
        }
        //check to see if the user also wants a new password
        if(!Objects.equals(requestBodyJson.get("new-password").asText(), "") && !Objects.equals(requestBodyJson.get("confirm-new-password").asText(), "")){
            return updateProfileWithNewPassword(requestBodyJson);
        }

        //if we arrived here, it means the user only wants to update his name
        String username = requestBodyJson.get("username").asText();
        User user = userRepository.findUserByUsername(username);

        if(!bCryptPasswordEncoder.matches(requestBodyJson.get("password").asText(), user.getPassword())){
            throw new AuthDataInvalid("Invalid credentials!");
        }

        user.setFirst_name(requestBodyJson.get("firstname").asText());
        user.setLast_name(requestBodyJson.get("lastname").asText());

        try {
            userRepository.save(user);
        } catch (Exception e){
            throw new DatabaseException("Database exception when updating profile");
        }
        return new ResponseEntity<>("Profiled updated!", HttpStatus.OK);
    }
    //updating profile + new password
    public ResponseEntity<String> updateProfileWithNewPassword(ObjectNode requestBodyJson) {
        String newPassword = requestBodyJson.get("new-password").asText();
        String confirmNewPassword = requestBodyJson.get("confirm-new-password").asText();
        //making sure that the new password was confirmed
        if (!newPassword.equals(confirmNewPassword)) {
            throw new AuthDataInvalid("Make sure new password and confirm new password fields match!");
        }
        String username = requestBodyJson.get("username").asText();
        User user = userRepository.findUserByUsername(username);

        if(!bCryptPasswordEncoder.matches(requestBodyJson.get("password").asText(), user.getPassword())){
            throw new AuthDataInvalid("Invalid credentials");
        }
        requestBodyJson.remove("password");
        requestBodyJson.remove("new-password");
        requestBodyJson.remove("confirm-password");
        //setting up the new data
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        user.setLast_name(requestBodyJson.get("lastname").asText());
        user.setFirst_name(requestBodyJson.get("firstname").asText());

        try {
            userRepository.save(user);
        } catch (Exception e){
            throw new DatabaseException("Database exception when updating profile");
        }
        return new ResponseEntity<>("Profiled updated!", HttpStatus.OK);
    }
    //we check to see whos the requester and whos the recipient
    public void setFriendshipModelData(UserDTO userDTO, User userToSee, User userToShow, Model model){
        if(friendshipRepository.existsFriendshipByRequesterIdAndRecipientIdAndPendingIsTrue(userToSee.getId(), userToShow.getId())){
            model.addAttribute("requester", true);
        }
        if(friendshipRepository.existsFriendshipByRequesterIdAndRecipientIdAndPendingIsTrue(userToShow.getId(), userToSee.getId())){
            model.addAttribute("recipient", true);
        }
        model.addAttribute("user", userDTO);
    }
}



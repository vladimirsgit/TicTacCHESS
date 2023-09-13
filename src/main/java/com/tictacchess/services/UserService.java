package com.tictacchess.services;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.bind.annotation.RequestBody;

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

    public ResponseEntity<String> confirmEmail(String username, String token){
        User user = userRepository.findUserByUsername(username);
        if(user == null) throw new UserNotFoundException("No user found.");
        if(user.getConfirmedEmail()) throw new UserAlreadyExistsException("Email is already confirmed!");

        if(!Objects.equals(user.getConfirmation_code(), token)){
            throw new AuthDataInvalid("Invalid data");
        }

        user.setConfirmedEmail(true);
        try {
            userRepository.save(user);
        } catch (Exception e){
            throw new DatabaseException("An error occurred while saving the user");
        }
        return new ResponseEntity<>("Email confirmed!", HttpStatus.ACCEPTED);
    }

    public String whatProfileDataToShow(String username, HttpSession httpSession, Model model){
        if(httpSession.getAttribute("username") == null){
            return "login";
        }
        if(httpSession.getAttribute("username").equals(username)){
            return "selfProfile";
        }
        return showOtherProfile(username, httpSession, model);
    }

    public String showOtherProfile(String username, HttpSession httpSession, Model model){
        User userToShow = userRepository.findUserByUsername(username);
        User userToSee = userRepository.findUserByUsername(httpSession.getAttribute("username").toString());

        if(userToShow == null || !userToShow.getConfirmedEmail()){
            return "redirect:/404";
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(userToShow.getUsername());
        userDTO.setRole(userToShow.getRole());
        userDTO.setCreatedAt(userToShow.getCreated_at());

        setFriendshipModelData(userDTO, userToSee, userToShow, model);

        return "otherProfile";
    }

    public ResponseEntity<String> updateProfile(ObjectNode requestBodyJson){
        if(!userRepository.existsUserByUsername(requestBodyJson.get("username").asText())){
            throw new UserNotFoundException("User not found!");
        }

        if(!Objects.equals(requestBodyJson.get("new-password").asText(), "") && !Objects.equals(requestBodyJson.get("confirm-new-password").asText(), "")){
            return updateProfileWithNewPassword(requestBodyJson);
        }

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

    public ResponseEntity<String> updateProfileWithNewPassword(ObjectNode requestBodyJson) {
        String newPassword = requestBodyJson.get("new-password").asText();
        String confirmNewPassword = requestBodyJson.get("confirm-new-password").asText();

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

    public void setFriendshipModelData(UserDTO userDTO, User userToSee, User userToShow, Model model){
        if(friendshipRepository.existsFriendshipByRequesterIdAndPendingIsTrue(userToSee.getId())){
            model.addAttribute("requester", true);
        }
        if(friendshipRepository.existsFriendshipByRequesterIdAndPendingIsTrue(userToShow.getId())){
            model.addAttribute("recipient", true);
        }
        model.addAttribute("user", userDTO);
    }
}



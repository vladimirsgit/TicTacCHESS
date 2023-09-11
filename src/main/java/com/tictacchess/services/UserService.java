package com.tictacchess.services;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tictacchess.dto.UserDTO;
import com.tictacchess.exceptions.AuthDataInvalid;
import com.tictacchess.exceptions.DatabaseException;
import com.tictacchess.exceptions.UserAlreadyExistsException;
import com.tictacchess.exceptions.UserNotFoundException;
import com.tictacchess.model.User;
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

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void confirmEmail(String username, String token){
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
    }

    public String whatProfileDataToShow(String username, HttpSession httpSession, Model model){
        if(httpSession.getAttribute("username") == null){
            return "login";
        }
        if(httpSession.getAttribute("username").equals(username)){
            return "selfProfile";
        }
        return showOtherProfile(username, model);
    }

    public String showOtherProfile(String username, Model model){
        User user = userRepository.findUserByUsername(username);

        if(user == null || !user.getConfirmedEmail()){
            return "redirect:/404";
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setUsername(user.getUsername());
        userDTO.setRole(user.getRole());
        userDTO.setCreatedAt(user.getCreated_at());

        model.addAttribute("user", userDTO);

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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credentials!");
        }

        user.setFirst_name(requestBodyJson.get("firstname").asText());
        user.setLast_name(requestBodyJson.get("lastname").asText());

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Profile updated!");


    }

    public ResponseEntity<String> updateProfileWithNewPassword(ObjectNode requestBodyJson) {
        String newPassword = requestBodyJson.get("new-password").asText();
        String confirmNewPassword = requestBodyJson.get("confirm-new-password").asText();

        if (!newPassword.equals(confirmNewPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Make sure new password and confirm new password fields match!");
        }
        String username = requestBodyJson.get("username").asText();
        User user = userRepository.findUserByUsername(username);

        if(!bCryptPasswordEncoder.matches(requestBodyJson.get("password").asText(), user.getPassword())){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid credentials");
        }
        requestBodyJson.remove("password");
        requestBodyJson.remove("new-password");
        requestBodyJson.remove("confirm-password");

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        user.setLast_name(requestBodyJson.get("lastname").asText());
        user.setFirst_name(requestBodyJson.get("firstname").asText());

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("Profile updated!");
    }
}



package com.tictacchess.backend.services;

import com.tictacchess.backend.dto.UserDTO;
import com.tictacchess.backend.model.User;
import com.tictacchess.backend.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Objects;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ResponseEntity<String> confirmEmail(String username, String token){
        User user = userRepository.findUserByUsername(username);
        if(user == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("An error occurred");
        if(Objects.equals(user.getConfirmation_code(), token)){
            user.setConfirmedEmail(true);
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Email confirmed!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("An error occurred");
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
        UserDTO userDTO = new UserDTO();

        userDTO.setUsername(user.getUsername());
        userDTO.setRole(user.getRole());
        userDTO.setCreatedAt(user.getCreated_at());

        model.addAttribute("user", userDTO);

        return "otherProfile";
    }
}



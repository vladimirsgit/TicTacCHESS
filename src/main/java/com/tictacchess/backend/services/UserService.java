package com.tictacchess.backend.services;

import com.tictacchess.backend.model.User;
import com.tictacchess.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public User saveUser(User user){
        return userRepository.save(user);
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
}



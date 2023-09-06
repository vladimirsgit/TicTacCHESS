package com.tictacchess.backend.services;

import com.tictacchess.backend.model.User;
import com.tictacchess.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    public ResponseEntity<String> registerUser(User user){
        String validateFieldsResponse = validateFields(user.getUsername(), user.getLast_name(), user.getFirst_name(), user.getEmail());

        if(!Objects.equals(validateFieldsResponse, "null")){
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validateFieldsResponse);
        }

        if(userRepository.existsUserByUsername(user.getUsername())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists!");
        }

        String hashedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body("You have been registered! Please check your email for validation!");
    }

    public String validateFields(String username, String last_name, String first_name, String email){
        String regExUsername = "^[A-Za-z0-9#_.]{3,49}$";
        String regExName = "^[A-Za-z]{1,100}$";
        String regExEmail = "^[A-Za-z0-9#_.-]+@[A-Za-z0-9-]+\\.com$";

        if(!username.matches(regExUsername)){
            return "Invalid username!";
        } else if(!last_name.matches(regExName) || !first_name.matches(regExName)){
            return "Invalid name!";
        } else if(!email.matches(regExEmail)){
            return "Invalid email!";
        }
        return "null";
    }

}

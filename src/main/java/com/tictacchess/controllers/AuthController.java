package com.tictacchess.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tictacchess.model.User;
import com.tictacchess.services.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody ObjectNode requestBodyJson){

        String confirmPassword = requestBodyJson.get("confirmPassword").asText();
        requestBodyJson.remove("confirmPassword");

        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.convertValue(requestBodyJson, User.class);

        if(!Objects.equals(confirmPassword, user.getPassword())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password and confirm password fields do not match!");
        }
        authService.registerUser(user);
        return new ResponseEntity<>("User registered! Please check your email for validation! :)", HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user, HttpSession httpSession){
        authService.verifyLogIn(user.getUsername(), user.getPassword(), httpSession);
        return new ResponseEntity<>("Welcome! :)", HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpSession httpSession){
        httpSession.invalidate();
        return new ResponseEntity<>("You have been logged out!", HttpStatus.ACCEPTED);
    }

}

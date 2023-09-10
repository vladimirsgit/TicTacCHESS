package com.tictacchess.backend.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tictacchess.backend.model.User;
import com.tictacchess.backend.services.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Stack;

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

        try{
            User user = objectMapper.convertValue(requestBodyJson, User.class);

            if(!Objects.equals(confirmPassword, user.getPassword())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password and confirm password fields do not match!");
            }
            return authService.registerUser(user);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User user, HttpSession httpSession){
        System.out.println("HELLLLLLLLLLLLLLLLLLO");
        return authService.verifyLogIn(user.getUsername(), user.getPassword(), httpSession);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpSession httpSession){
        httpSession.invalidate();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("You have been logged out!");
    }
}

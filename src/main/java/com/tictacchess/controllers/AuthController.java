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
        return authService.registerUser(requestBodyJson);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody User possibleUser, HttpSession httpSession){
        return authService.verifyLogIn(possibleUser, httpSession);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpSession httpSession){
        return authService.logoutUser(httpSession);
    }
}

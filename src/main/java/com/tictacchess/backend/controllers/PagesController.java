package com.tictacchess.backend.controllers;

import com.tictacchess.backend.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PagesController {

    private final UserService userService;

    public PagesController(UserService userService){
        this.userService = userService;
    }

    @GetMapping(value = {"/", "/home", "/index"})
    public String home(){
        return "index";
    }

    @GetMapping("/register")
    public String registerForm(){
        return "register";
    }

    @GetMapping("/login")
    public String loginForm(){
        return "login";
    }

    @GetMapping("/profile/{username}")
    public String showProfileData(@PathVariable String username, HttpSession httpSession, Model model){
        return userService.whatProfileDataToShow(username, httpSession, model);
    }
//    @GetMapping("/profile")
//    public ResponseEntity<String> getProfile(HttpSession httpSession){
//        return ResponseEntity.status(HttpStatus.OK).body(httpSession.getAttribute("username").toString());
//    }
}

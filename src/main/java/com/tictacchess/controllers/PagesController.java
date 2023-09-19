package com.tictacchess.controllers;

import com.tictacchess.services.FriendshipService;
import com.tictacchess.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PagesController {

    private final UserService userService;
    private final FriendshipService friendshipService;
    public PagesController(UserService userService, FriendshipService friendshipService){
        this.userService = userService;
        this.friendshipService = friendshipService;
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

    @GetMapping("/friends")
    public String showListOfFriends(HttpSession httpSession, Model model){
        return friendshipService.showListOfFriends(httpSession, model);
    }
}

package com.tictacchess.backend.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tictacchess.backend.model.User;
import com.tictacchess.backend.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
         this.userService = userService;
    }

    @GetMapping("/{username}/{token}")
    public ResponseEntity<String> confirmEmail(@PathVariable String username, @PathVariable String token){
        return userService.confirmEmail(username, token);
    }

    @PostMapping("/updateProfile")
    @ResponseBody
    public ResponseEntity<String> updateProfile(@RequestBody ObjectNode requestBodyJson){
        return userService.updateProfile(requestBodyJson);
    }
}

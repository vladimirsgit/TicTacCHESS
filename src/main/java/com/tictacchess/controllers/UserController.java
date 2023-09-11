package com.tictacchess.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tictacchess.services.UserService;
import org.springframework.http.HttpStatus;
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
        userService.confirmEmail(username, token);
        return new ResponseEntity<>("Email confirmed!", HttpStatus.ACCEPTED);
    }

    @PostMapping("/updateProfile")
    @ResponseBody
    public ResponseEntity<String> updateProfile(@RequestBody ObjectNode requestBodyJson){
        return userService.updateProfile(requestBodyJson);
    }

}

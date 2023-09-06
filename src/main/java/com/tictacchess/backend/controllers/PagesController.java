package com.tictacchess.backend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PagesController {
    @GetMapping(value = {"/", "/home", "/index"})
    public String home(){
        return "index";
    }

    @GetMapping("/register")
    public String registerForm(){
        return "register";
    }

}

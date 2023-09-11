package com.tictacchess.services;

import com.tictacchess.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    public void setSessionData(User user, HttpSession httpSession){
         httpSession.setAttribute("username", user.getUsername());
         httpSession.setAttribute("email", user.getEmail());
         httpSession.setAttribute("lastname", user.getLast_name());
         httpSession.setAttribute("firstname", user.getFirst_name());
         httpSession.setAttribute("role", user.getRole());
    }
//    @RequestMapping("/api/sessionInfo")
//    public ResponseEntity<String> getSessionInfo(HttpSession httpSession){
//        return ResponseEntity.status(HttpStatus.ACCEPTED).body(httpSession.getAttribute("username").toString());
//    }
}
package com.tictacchess.services;

import com.tictacchess.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionService {
    //setting session data for logging in
    public void setSessionData(User user, HttpSession httpSession){
         httpSession.setAttribute("username", user.getUsername());
         httpSession.setAttribute("email", user.getEmail());
         httpSession.setAttribute("lastname", user.getLast_name());
         httpSession.setAttribute("firstname", user.getFirst_name());
         httpSession.setAttribute("role", user.getRole());
    }
}

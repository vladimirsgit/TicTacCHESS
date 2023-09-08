package com.tictacchess.backend.services;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import com.tictacchess.backend.model.User;

@Service
public class SessionService {
    public void setSessionData(User user, HttpSession httpSession){
         httpSession.setAttribute("username", user.getUsername());
         httpSession.setAttribute("email", user.getEmail());
         httpSession.setAttribute("lastname", user.getLast_name());
         httpSession.setAttribute("firstname", user.getFirst_name());
         httpSession.setAttribute("role", user.getRole());
    }
}

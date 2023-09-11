package com.tictacchess.backend.services;

import com.tictacchess.backend.exceptions.*;
import com.tictacchess.backend.model.User;
import com.tictacchess.backend.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, EmailService emailService){
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.emailService = emailService;
    }


    public void registerUser(User user){
        String validateFieldsResponse = validateFields(user.getUsername(), user.getLast_name(), user.getFirst_name(), user.getEmail());

        if(!Objects.equals(validateFieldsResponse, "null")){
            throw new AuthDataInvalid(validateFieldsResponse);
        }

        if(userRepository.existsUserByUsername(user.getUsername())){
            throw new UserAlreadyExistsException("Username taken!");
        }

        String hashedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        user.setConfirmation_code(TokenGenerator.generateSecureToken(64));

        try {
            String htmlContent = "<div style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; color: #333;\">" +
                    "<div style=\"width: 100%; margin: auto; background-color: #fff; padding: 20px; border-radius: 8px;\">" +
                    "<h1>Hi there!</h1>" +
                    "<p> Welcome to TicTacChess</p>" +
                    "<p>To confirm your email, please click the link below:</p>" +
                    "<a href=\"http://www.localhost:8080/user/" +
                    user.getUsername() + "/" + user.getConfirmation_code() + "\" style=\"background-color: #007bff; color: white; padding: 10px 20px; text-align: center; text-decoration: none; display: inline-block; margin: 10px 2px; cursor: pointer; border-radius: 4px;\">Confirm Email</a>" +
                    "</div>" +
                    "</div>";

            emailService.sendEmail(user.getEmail(), "Confirm your email", htmlContent);
        } catch (MessagingException messagingException){
            throw new EmailException("There was a problem with the email, please try again later.");
        }

        try {
            userRepository.save(user);
        } catch (Exception e){
            throw new DatabaseException("A database error occurred while saving the user");
        }

    }

    public String validateFields(String username, String last_name, String first_name, String email){
        String regExUsername = "^[A-Za-z0-9#_.]{3,10}$";
        String regExName = "^[A-Za-z]{1,100}$";
        String regExEmail = "^[A-Za-z0-9#_.-]+@[A-Za-z0-9-]+\\.com$";

        if(!username.matches(regExUsername)){
            return "Invalid username!";
        } else if(!last_name.matches(regExName) || !first_name.matches(regExName)){
            return "Invalid name!";
        } else if(!email.matches(regExEmail)){
            return "Invalid email!";
        }
        return "null";
    }

    public void verifyLogIn(String username, String password, HttpSession httpSession){

        User user = userRepository.findUserByUsername(username);

        if(user == null) throw new UserNotFoundException("Invalid credentials!");

        if(!user.getConfirmedEmail()) throw new EmailException("Please confirm your email!");

        if(!bCryptPasswordEncoder.matches(password, user.getPassword())){
            throw new AuthDataInvalid("Invalid credentials!");
        }
        SessionService sessionService = new SessionService();
        sessionService.setSessionData(user, httpSession);
    }

}

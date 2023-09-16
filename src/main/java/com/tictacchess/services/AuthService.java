package com.tictacchess.services;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tictacchess.exceptions.*;
import com.tictacchess.model.User;
import com.tictacchess.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    //registering the user
    public ResponseEntity<String> registerUser(ObjectNode requestBodyJson){
        User user = verifyRegister(requestBodyJson);
        String hashedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        //we encode the password and generate a token for email confirmation
        user.setPassword(hashedPassword);
        user.setConfirmation_code(TokenGenerator.generateSecureToken(64));

        sendRegisteringEmail(user);

        try {
            userRepository.save(user);
        } catch (Exception e){
            throw new DatabaseException("A database error occurred while saving the user");
        }
        return new ResponseEntity<>("User registered! Please check your email for validation! :)", HttpStatus.CREATED);
    }
    //registration field validation using regEx
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
    //checks registration data
    public User verifyRegister(ObjectNode requestBodyJson){
        if(requestBodyJson == null || requestBodyJson.get("confirmPassword") == null){
            throw new AuthDataInvalid("Empty fields.");
        }
        String confirmPassword = requestBodyJson.get("confirmPassword").asText();
        requestBodyJson.remove("confirmPassword");
        //we remove the confirmPassword property from the JSON so we can map the requestBody to the user, as the user doesnt have that field
        ObjectMapper objectMapper = new ObjectMapper();
        User user = objectMapper.convertValue(requestBodyJson, User.class);

        if(!Objects.equals(confirmPassword, user.getPassword())){
            throw new AuthDataInvalid("Password and confirm password fields do not match!");
        }

        //we validate all the fields with regEx
        String validateFieldsResponse = validateFields(user.getUsername(), user.getLast_name(), user.getFirst_name(), user.getEmail());
        //if the return value of validate fields is not null, means something went wrong
        if(!Objects.equals(validateFieldsResponse, "null")){
            throw new AuthDataInvalid(validateFieldsResponse);
        }

        if(userRepository.existsUserByUsername(user.getUsername())){
            throw new UserAlreadyExistsException("Username taken!");
        }
        return user;
    }

    public void sendRegisteringEmail(User user){
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
    }

    public ResponseEntity<String> verifyLogIn(User possibleUser, HttpSession httpSession) {
        if(possibleUser == null) throw new AuthDataInvalid("Invalid data");
        String password = possibleUser.getPassword();
        User user = userRepository.findUserByUsername(possibleUser.getUsername());

        if (user == null) throw new UserNotFoundException("Invalid credentials!");
        if (!user.getConfirmedEmail()) throw new EmailException("Please confirm your email!");

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new AuthDataInvalid("Invalid credentials!");
        }
        SessionService sessionService = new SessionService();
        sessionService.setSessionData(user, httpSession);

        return new ResponseEntity<>("Welcome! :)", HttpStatus.OK);
    }

    public ResponseEntity<String> logoutUser(HttpSession httpSession){
        httpSession.invalidate();
        return new ResponseEntity<>("You have been logged out!", HttpStatus.ACCEPTED);
    }
}

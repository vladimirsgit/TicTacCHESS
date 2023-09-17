package com.tictacchess.servicesTests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tictacchess.exceptions.AuthDataInvalid;
import com.tictacchess.exceptions.UserAlreadyExistsException;
import com.tictacchess.exceptions.UserNotFoundException;
import com.tictacchess.model.User;
import com.tictacchess.repository.FriendshipRepository;
import com.tictacchess.repository.UserRepository;
import com.tictacchess.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private FriendshipRepository friendshipRepository;

    @InjectMocks
    private UserService userService;

    //!tests for userService.confirmEmail()
    @Test
    public void testConfirmEmailPass(){
        User user = mock(User.class);;

        when(userRepository.findUserByUsername(any())).thenReturn(user);
        when(user.getConfirmedEmail()).thenReturn(false);
        when(user.getConfirmation_code()).thenReturn("");

        ResponseEntity<String> responseEntity = userService.confirmEmail("", "");

        assertEquals("Email confirmed!", responseEntity.getBody());
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void testConfirmEmailUserNotFound(){
        when(userRepository.findUserByUsername(any())).thenReturn(null);

        UserNotFoundException thrownException = assertThrows(UserNotFoundException.class, () -> {
            userService.confirmEmail("", "");
        });
        assertEquals("No user found.", thrownException.getMessage());
    }

    @Test
    public void testConfirmEmailAlreadyConfirmed(){
        User user = mock(User.class);

        when(userRepository.findUserByUsername(any())).thenReturn(user);
        when(user.getConfirmedEmail()).thenReturn(true);

        UserAlreadyExistsException thrownException = assertThrows(UserAlreadyExistsException.class, () -> {
            userService.confirmEmail("", "");
        });
        assertEquals("Email is already confirmed!", thrownException.getMessage());
    }

    //!tests for userService.whatProfileDataToShow()
    @Test
    public void testWhatProfileDataToShowUserNotLoggedIn(){
        HttpSession httpSession = mock(HttpSession.class);
        Model model = mock(Model.class);

        when(httpSession.getAttribute("username")).thenReturn(null);

        String returnValue = userService.whatProfileDataToShow("", httpSession, model);
        assertEquals("login", returnValue);
    }

    @Test
    public void testWhatProfileDataToShowSelfProfile(){
        HttpSession httpSession = mock(HttpSession.class);
        Model model = mock(Model.class);

        when(httpSession.getAttribute("username")).thenReturn("not null");

        String returnValue = userService.whatProfileDataToShow("not null", httpSession, model);
        assertEquals("selfProfile", returnValue);
    }

    //!tests for userService.showOtherProfile()
    @Test
    public void testShowOtherProfilePass(){
        HttpSession httpSession = mock(HttpSession.class);
        User user = mock(User.class);
        Model model = new ExtendedModelMap();

        when(userRepository.findUserByUsername(any())).thenReturn(user);
        when(httpSession.getAttribute(any())).thenReturn("not null");
        when(user.getConfirmedEmail()).thenReturn(true);
        //means that there is a friendship request and the viewer is the requester
        when(friendshipRepository.existsFriendshipByRequesterIdAndRecipientIdAndPendingIsTrue(any(), any()))
                .thenReturn(true);

        String returnValue = userService.showOtherProfile("", httpSession, model);
        assertEquals("otherProfile", returnValue);
        assertTrue(model.containsAttribute("requester"));
        assertEquals(true, model.getAttribute("requester"));
    }

    @Test
    public void testShowOtherProfileUserNotFound(){
        HttpSession httpSession = mock(HttpSession.class);
        User user = mock(User.class);
        Model model = mock(Model.class);

        when(userRepository.findUserByUsername(any())).thenReturn(null);
        when(httpSession.getAttribute(any())).thenReturn("not null");
        String returnValue = userService.showOtherProfile("", httpSession, model);

        assertEquals("redirect:/404", returnValue);
    }
    //!tests for userService.updateProfile()
    @Test
    public void testUpdateProfileNoNewPasswordPass(){
        ObjectNode objectNode = mock(ObjectNode.class);
        JsonNode jsonNode = mock(JsonNode.class);
        User user = mock(User.class);

        when(userRepository.existsUserByUsername(any())).thenReturn(true);
        when(userRepository.findUserByUsername(any())).thenReturn(user);
        when(objectNode.get(any())).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("");
        when(user.getPassword()).thenReturn("");
        when(bCryptPasswordEncoder.matches(any(), any())).thenReturn(true);

        ResponseEntity<String> responseEntity = userService.updateProfile(objectNode);
        assertEquals("Profile updated!", responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testUpdateProfileNewPasswordPass(){
        ObjectNode objectNode = mock(ObjectNode.class);
        JsonNode jsonNode = mock(JsonNode.class);
        User user = mock(User.class);

        when(userRepository.existsUserByUsername(any())).thenReturn(true);
        when(objectNode.get(any())).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("newpass");
        when(userRepository.findUserByUsername(any())).thenReturn(user);
        when(bCryptPasswordEncoder.matches(any(), any())).thenReturn(true);

        ResponseEntity<String> responseEntity = userService.updateProfile(objectNode);
        assertEquals("Profile updated!", responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testUpdateProfileInvalidPassword(){
        ObjectNode objectNode = mock(ObjectNode.class);
        JsonNode jsonNode = mock(JsonNode.class);
        User user = mock(User.class);

        when(userRepository.existsUserByUsername(any())).thenReturn(true);
        when(objectNode.get(any())).thenReturn(jsonNode);
        when(jsonNode.asText()).thenReturn("");
        when(userRepository.findUserByUsername(any())).thenReturn(user);
        when(bCryptPasswordEncoder.matches(any(), any())).thenReturn(false);

        AuthDataInvalid thrownException = assertThrows(AuthDataInvalid.class, () -> {
            userService.updateProfile(objectNode);
        });
        assertEquals("Invalid credentials!", thrownException.getMessage());
    }
    //!tests for userService.updateProfileWithNewPassword()
    @Test
    public void testUpdateProfileWithNewPasswordPasswordsDoNotMatch(){
        ObjectNode objectNode = mock(ObjectNode.class);
        JsonNode jsonNode1 = mock(JsonNode.class);
        JsonNode jsonNode2 = mock(JsonNode.class);;

        when(objectNode.get("new-password")).thenReturn(jsonNode1);
        when(objectNode.get("confirm-new-password")).thenReturn(jsonNode2);
        when(jsonNode1.asText()).thenReturn("newpass");
        when(jsonNode2.asText()).thenReturn("confirmnewpass");

        AuthDataInvalid thrownException = assertThrows(AuthDataInvalid.class, () -> {
            userService.updateProfileWithNewPassword(objectNode);
        });
        assertEquals("Make sure new password and confirm new password fields match!", thrownException.getMessage());
    }



}

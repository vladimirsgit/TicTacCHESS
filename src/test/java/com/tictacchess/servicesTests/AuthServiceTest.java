package com.tictacchess.servicesTests;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tictacchess.TestUtils.TestUtils;
import com.tictacchess.exceptions.AuthDataInvalid;
import com.tictacchess.exceptions.EmailException;
import com.tictacchess.exceptions.UserAlreadyExistsException;
import com.tictacchess.exceptions.UserNotFoundException;
import com.tictacchess.model.User;
import com.tictacchess.repository.UserRepository;
import com.tictacchess.services.AuthService;
import com.tictacchess.services.EmailService;
import com.tictacchess.services.SessionService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    EmailService emailService;

    @InjectMocks
    AuthService authService;

    //! tests for authService.registerUser()
    @Test
    public void testRegisterUserPass() throws Exception {
        //Arrange
        ObjectNode requestBodyJson = TestUtils.createRequestBodyJson("aaaa", "aaaa", "email@example.com", "1234aaaa", "a", "a");

        //Act
        ResponseEntity<String> responseEntity = authService.registerUser(requestBodyJson);

        //Assert
        assertEquals(new ResponseEntity<>("User registered! Please check your email for validation! :)", HttpStatus.CREATED), responseEntity);

        verify(bCryptPasswordEncoder, times(1)).encode(any());
        verify(emailService, times(1)).sendEmail(any(), any(), any());
        verify(userRepository, times(1)).save(any());
        verify(userRepository, times(1)).existsUserByUsername(anyString());
    }

    @Test
    public void testRegisterUserUsernameTaken() throws Exception{

        ObjectNode requestBodyJson = TestUtils.createRequestBodyJson("aaaa", "aaaa", "email@example.com", "1234aaaa", "a", "a");
        when(userRepository.existsUserByUsername(anyString())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> {
            ResponseEntity<String> responseEntity = authService.registerUser(requestBodyJson);
        });

        verify(bCryptPasswordEncoder, times(0)).encode(any());
        verify(emailService, times(0)).sendEmail(any(), any(), any());
        verify(userRepository, times(0)).save(any());
        verify(userRepository, times(1)).existsUserByUsername(anyString());
    }

    @Test
    public void testRegisterUserSendEmailFailed() throws MessagingException {
        ObjectNode requestBodyJson = TestUtils.createRequestBodyJson("aaaa", "aaaa", "email@example.com", "1234aaaa", "a", "a");
        //used for methods that return VOID
        doThrow(new MessagingException())
                .when(emailService).sendEmail(any(), any(), any());

        assertThrows(EmailException.class, () -> {
            authService.registerUser(requestBodyJson);
        });

        verify(userRepository, times(0)).save(any());
        verify(bCryptPasswordEncoder, times(1)).encode(any());
        verify(userRepository, times(1)).existsUserByUsername(anyString());
    }

    //! tests for authService.verifyRegister()
    @Test
    public void testVerifyRegisterWithRequestBodyJsonNull() {
        ObjectNode requestBodyJson = null;

        AuthDataInvalid thrownException = assertThrows(AuthDataInvalid.class, () -> {
           authService.verifyRegister(requestBodyJson);
        });

        assertEquals("Empty fields.", thrownException.getMessage());
        verify(userRepository, times(0)).existsUserByUsername(anyString());
    }

    @Test
    public void testVerifyRegisterPasswordFieldsNotMatching(){
        ObjectNode requestBodyJson = TestUtils.createRequestBodyJson("aaaa", "aaaa", "email@example.com",
                "1234aaaa", "a", "ab");

        AuthDataInvalid thrownException = assertThrows(AuthDataInvalid.class, () -> {
            authService.verifyRegister(requestBodyJson);
        });
        assertEquals("Password and confirm password fields do not match!", thrownException.getMessage());
    }

    @Test
    public void testVerifyRegisterInvalidNames(){
        ObjectNode requestBodyJson = TestUtils.createRequestBodyJson("1234", "aaaa", "email@example.com",
                "1234aaaa", "a", "a");

        AuthDataInvalid thrownException = assertThrows(AuthDataInvalid.class, () -> {
            authService.verifyRegister(requestBodyJson);
        });
        assertEquals("Invalid name!", thrownException.getMessage());
    }
    @Test
    public void testVerifyRegisterInvalidEmail(){
        ObjectNode requestBodyJson = TestUtils.createRequestBodyJson("aaaa", "aaaa", "emailexample.com",
                "1234aaaa", "a", "a");

        AuthDataInvalid thrownException = assertThrows(AuthDataInvalid.class, () -> {
            authService.verifyRegister(requestBodyJson);
        });
        assertEquals("Invalid email!", thrownException.getMessage());
    }

    //!tests for authService.verifyLogin
    @Test
    public void testVerifyLoginPass(){
        User user = mock(User.class);
        HttpSession httpSession = mock(HttpSession.class);

        when(userRepository.findUserByUsername(any())).thenReturn(user);
        when(user.getConfirmedEmail()).thenReturn(true);
        when(bCryptPasswordEncoder.matches(any(), any())).thenReturn(true);

        assertEquals("Welcome! :)", authService.verifyLogIn(user, httpSession).getBody());
    }

    @Test
    public void testVerifyLoginUsernameNotFound(){
        User user = mock(User.class);
        HttpSession httpSession = mock(HttpSession.class);

        when(userRepository.findUserByUsername(any())).thenReturn(null);

        UserNotFoundException thrownException = assertThrows(UserNotFoundException.class, () -> {
           authService.verifyLogIn(user, httpSession);
        });

        assertEquals("Invalid credentials!", thrownException.getMessage());
        verify(user, times(0)).getConfirmedEmail();
    }

    @Test
    public void testVerifyLoginWrongPassword(){
        User user = mock(User.class);
        HttpSession httpSession = mock(HttpSession.class);

        when(userRepository.findUserByUsername(any())).thenReturn(user);
        when(user.getConfirmedEmail()).thenReturn(true);
        when(bCryptPasswordEncoder.matches(any(), any())).thenReturn(false);

        AuthDataInvalid thrownException = assertThrows(AuthDataInvalid.class, () -> {
           authService.verifyLogIn(user, httpSession);
        });
        assertEquals("Invalid credentials!", thrownException.getMessage());
    }



}

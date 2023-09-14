package com.tictacchess.servicesTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tictacchess.TestUtils.TestUtils;
import com.tictacchess.exceptions.UserAlreadyExistsException;
import com.tictacchess.model.User;
import com.tictacchess.repository.UserRepository;
import com.tictacchess.services.AuthService;
import com.tictacchess.services.EmailService;
import com.tictacchess.services.TokenGenerator;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    public void testRegisterUserPass() throws Exception {
        //Arrange
        ObjectNode requestBodyJson = TestUtils.createRequestBodyJson("aaaa", "aaaa", "vladimir_stratulat99@yahoo.com", "1234aaaa", "a", "a");

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

        ObjectNode requestBodyJson = TestUtils.createRequestBodyJson("aaaa", "aaaa", "vladimir_stratulat99@yahoo.com", "1234aaaa", "a", "a");
        when(userRepository.existsUserByUsername(anyString())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> {
            ResponseEntity<String> responseEntity = authService.registerUser(requestBodyJson);
        });
        verify(bCryptPasswordEncoder, times(0)).encode(any());
        verify(emailService, times(0)).sendEmail(any(), any(), any());
        verify(userRepository, times(0)).save(any());
        verify(userRepository, times(1)).existsUserByUsername(anyString());
    }
}

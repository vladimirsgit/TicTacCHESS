package com.tictacchess.controllersTests;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tictacchess.controllers.AuthController;
import com.tictacchess.exceptions.AuthDataInvalid;
import com.tictacchess.services.AuthService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @MockBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    private String json;
    @BeforeEach
    public void setUp(){
        json = "{\"username\":\"john\",\"password\":\"12345\"}";
    }

    @Test
    public void testRegisterUser() throws Exception {
        when(authService.registerUser(any(ObjectNode.class))).thenReturn(new ResponseEntity<>("User registered! Please check your email for validation! :)", HttpStatus.CREATED));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered! Please check your email for validation! :)"));
    }

    @Test
    public void testLoginIsOk() throws Exception {
        when(authService.verifyLogIn(any(String.class), any(String.class), any(HttpSession.class))).thenReturn(new ResponseEntity<>("Welcome! :)", HttpStatus.OK));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Welcome! :)"));

    }

    @Test
    public void testLoginFailed() throws Exception {
        when(authService.verifyLogIn(any(String.class), any(String.class), any(HttpSession.class))).thenThrow(new AuthDataInvalid("Invalid credentials!"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid credentials!"));
    }
}

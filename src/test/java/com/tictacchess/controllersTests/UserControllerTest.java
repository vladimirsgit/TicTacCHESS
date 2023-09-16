package com.tictacchess.controllersTests;

import com.tictacchess.controllers.UserController;
import com.tictacchess.services.UserService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testConfirmEmail() throws Exception {
        when(userService.confirmEmail(anyString(), anyString())).thenReturn(new ResponseEntity<>("Email confirmed", HttpStatus.OK));

        mockMvc.perform(get("/user/username/token"))
                .andExpect(status().isOk())
                .andExpect(content().string("Email confirmed"));
    }

    @Test
    public void testUpdateProfile() throws Exception {
        when(userService.updateProfile(any())).thenReturn(new ResponseEntity<>("Profile updated!", HttpStatus.OK));
        String json = "{}";
        mockMvc.perform(post("/user/updateProfile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Profile updated!"));
    }
}

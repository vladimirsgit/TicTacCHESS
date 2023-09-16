package com.tictacchess.controllersTests;

import com.tictacchess.controllers.AuthController;
import com.tictacchess.controllers.FriendshipController;
import com.tictacchess.model.Friendship;
import com.tictacchess.services.FriendshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FriendshipController.class)
@ExtendWith(MockitoExtension.class)
public class FriendshipControllerTest {
    @MockBean
    private FriendshipService friendshipService;

    @Autowired
    private MockMvc mockMvc;

    private String json;
    @BeforeEach
    public void setUp(){
        json = "{\"recipientUsername\":\"john\",\"requesterUsername\":\"12345\"}";
    }
    @Test
    public void testAddFriendPath() throws Exception {
        when(friendshipService.addFriend(any(), any())).thenReturn(new ResponseEntity<>("Friend request sent!", HttpStatus.OK));

        mockMvc.perform(post("/api/friends/addFriend")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Friend request sent!"));
    }

    @Test
    public void testAcceptFriendshipPath() throws Exception {
        when(friendshipService.acceptFriendship(anyString(), any())).thenReturn(new ResponseEntity<>("Friendship accepted", HttpStatus.OK));

        mockMvc.perform(post("/api/friends/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk()).
                andExpect(content().string("Friendship accepted"));
    }

    @Test
    public void testDeclineFriendshipPath() throws Exception {
        when(friendshipService.declineFriendship(anyString(), any())).thenReturn(new ResponseEntity<>("Friendship declined", HttpStatus.OK));

        mockMvc.perform(post("/api/friends/decline")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Friendship declined"));
    }
}

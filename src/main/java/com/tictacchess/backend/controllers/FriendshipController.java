package com.tictacchess.backend.controllers;
import com.tictacchess.backend.dto.FriendshipRequestDTO;
import com.tictacchess.backend.services.FriendshipService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friends")
public class FriendshipController {
    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService){
        this.friendshipService = friendshipService;
    }

    @PostMapping("/addFriend")
    public ResponseEntity<String> sendFriendshipRequestData(@RequestBody FriendshipRequestDTO friendshipRequestDTO){
        friendshipService.addFriend(friendshipRequestDTO.getRequesterUsername(), friendshipRequestDTO.getRecipientUsername());
        return new ResponseEntity<>("Friend request sent!", HttpStatus.OK);

    }
}

package com.tictacchess.controllers;
import com.tictacchess.dto.FriendshipRequestDTO;
import com.tictacchess.services.FriendshipService;
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
        return friendshipService.addFriend(friendshipRequestDTO.getRequesterUsername(), friendshipRequestDTO.getRecipientUsername());
    }
}

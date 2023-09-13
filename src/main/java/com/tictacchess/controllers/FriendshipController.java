package com.tictacchess.controllers;
import com.tictacchess.dto.FriendshipRequestDTO;
import com.tictacchess.services.FriendshipService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/friends")
public class FriendshipController {
    private final FriendshipService friendshipService;

    public FriendshipController(FriendshipService friendshipService){
        this.friendshipService = friendshipService;
    }

    @PostMapping("/addFriend")
    public ResponseEntity<String> addFriend(@RequestBody FriendshipRequestDTO friendshipRequestDTO, HttpSession httpSession){
        return friendshipService.addFriend(friendshipRequestDTO.getRecipientUsername(), httpSession);
    }

    @PostMapping("/accept")
    public ResponseEntity<String> acceptFriendship(@RequestBody FriendshipRequestDTO friendshipRequestDTO, HttpSession httpSession){
        return friendshipService.acceptFriendship(friendshipRequestDTO.getRequesterUsername(), httpSession);
    }

    @PostMapping("/decline")
    public ResponseEntity<String> declineFriendship(@RequestBody FriendshipRequestDTO friendshipRequestDTO, HttpSession httpSession){
        return friendshipService.declineFriendship(friendshipRequestDTO.getRequesterUsername(), httpSession);
    }
}

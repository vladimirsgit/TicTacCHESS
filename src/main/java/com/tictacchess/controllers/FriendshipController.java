package com.tictacchess.controllers;
import com.tictacchess.dto.FriendDeletionDTO;
import com.tictacchess.dto.FriendshipRequestDTO;
import com.tictacchess.services.FriendshipService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/cancelRequest")
    public ResponseEntity<String> cancelRequest(@RequestBody FriendshipRequestDTO friendshipRequestDTO, HttpSession httpSession){
        return friendshipService.cancelRequest(friendshipRequestDTO.getRecipientUsername(), httpSession);
    }

    @PostMapping("/accept")
    public ResponseEntity<String> acceptFriendship(@RequestBody FriendshipRequestDTO friendshipRequestDTO, HttpSession httpSession){
        return friendshipService.acceptFriendship(friendshipRequestDTO.getRequesterUsername(), httpSession);
    }

    @PostMapping("/decline")
    public ResponseEntity<String> declineFriendship(@RequestBody FriendshipRequestDTO friendshipRequestDTO, HttpSession httpSession){
        return friendshipService.declineFriendship(friendshipRequestDTO.getRequesterUsername(), httpSession);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFriend(@RequestBody FriendDeletionDTO friendDeletionDTO, HttpSession httpSession){
        return friendshipService.deleteFriend(friendDeletionDTO.getFriend(), httpSession);
    }
}

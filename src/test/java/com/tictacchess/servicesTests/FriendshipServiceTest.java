package com.tictacchess.servicesTests;

import com.tictacchess.exceptions.AddFriendException;
import com.tictacchess.exceptions.DatabaseException;
import com.tictacchess.exceptions.FriendshipAlreadyExistsException;
import com.tictacchess.exceptions.UserNotFoundException;
import com.tictacchess.model.Friendship;
import com.tictacchess.model.User;
import com.tictacchess.repository.FriendshipRepository;
import com.tictacchess.repository.UserRepository;
import com.tictacchess.services.FriendshipService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FriendshipServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    @InjectMocks
    private FriendshipService friendshipService;

    private HttpSession httpSession;
    private User user;
    @BeforeEach
    public void setUp(){
        httpSession = mock(HttpSession.class);
        user = mock(User.class);
    }

    //!tests for friendshipService.addFriend()
    @Test
    public void testAddFriendPassed(){
        when(friendshipRepository.existsFriendshipByRecipientIdAndRequesterId(any(), anyInt())).thenReturn(false);
        when(httpSession.getAttribute(any())).thenReturn("not null");
        when(userRepository.findUserByUsername(any())).thenReturn(user);
        when(friendshipRepository.save(any())).thenReturn(null);

        assertEquals("Friend request sent!", friendshipService.addFriend("", httpSession).getBody());
        verify(friendshipRepository, times(1)).save(any(Friendship.class));
    }

    @Test
    public void testAddFriendUserNotLoggedIn(){
        when(httpSession.getAttribute(any())).thenReturn(null);

        UserNotFoundException thrownException = assertThrows(UserNotFoundException.class, () -> {
           friendshipService.addFriend("", httpSession);
        });
        assertEquals("Please log in before sending friend requests!", thrownException.getMessage());
        verify(httpSession, times(1)).getAttribute(any());
    }

    @Test
    public void testAddFriendUserNotFoundInDatabase(){
        when(userRepository.findUserByUsername(any())).thenReturn(null);
        when(httpSession.getAttribute("username")).thenReturn("not null");

        UserNotFoundException thrownException = assertThrows(UserNotFoundException.class, () -> {
            friendshipService.addFriend("", httpSession);
        });
        assertEquals("Something is wrong with the username/usernames", thrownException.getMessage());
    }

    @Test
    public void testAddFriendFriendshipExists(){
        when(friendshipRepository.existsFriendshipByRecipientIdAndRequesterId(any(), anyInt())).thenReturn(true);
        when(httpSession.getAttribute(any())).thenReturn("not null");
        when(userRepository.findUserByUsername(any())).thenReturn(user);

        FriendshipAlreadyExistsException thrownException = assertThrows(FriendshipAlreadyExistsException.class, () -> {
            friendshipService.addFriend("", httpSession);
        });
        assertEquals("Friendship or friend request already exists!", thrownException.getMessage());
        verify(friendshipRepository, times(0)).save(any());
    }

    @Test
    public void testAddFriendFriendshipSaveError(){
        when(friendshipRepository.existsFriendshipByRecipientIdAndRequesterId(any(), any())).thenReturn(false);
        when(httpSession.getAttribute(any())).thenReturn("not null");
        when(userRepository.findUserByUsername(any())).thenReturn(user);
        doThrow(new RuntimeException()).when(friendshipRepository).save(any());

        DatabaseException thrownException = assertThrows(DatabaseException.class, () -> {
           friendshipService.addFriend("", httpSession);
        });
        assertEquals("A database error occurred while trying to save the friendship request.", thrownException.getMessage());
    }

    //!tests for friendshipService.acceptFriendship()
    @Test
    public void testAcceptFriendshipPassed(){
        when(httpSession.getAttribute(any())).thenReturn("not null");
        when(userRepository.findUserByUsername(any())).thenReturn(user);
        when(friendshipRepository.existsFriendshipByRequesterIdAndRecipientIdAndPendingIsTrue(any(), any())).thenReturn(true);
        when(friendshipRepository.existsFriendshipByRequesterIdAndRecipientIdAndDeclinedIsTrue(any(), any())).thenReturn(false);

        assertEquals("Friend request accepted!", friendshipService.acceptFriendship("", httpSession).getBody());
    }

    @Test
    public void testAcceptFriendshipRequesterNotFound(){
        when(httpSession.getAttribute(any())).thenReturn("not null");
        when(userRepository.findUserByUsername(any())).thenReturn(null);

        UserNotFoundException thrownException = assertThrows(UserNotFoundException.class, () -> {
            friendshipService.acceptFriendship("", httpSession);
        });
        assertEquals("User does not exist!", thrownException.getMessage());
    }

    @Test
    public void testAcceptFriendshipNoFriendRequestAvailable(){
        when(httpSession.getAttribute(any())).thenReturn("not null");
        when(userRepository.findUserByUsername(any())).thenReturn(user);
        //no friend request found
        when(friendshipRepository.existsFriendshipByRequesterIdAndRecipientIdAndPendingIsTrue(any(), any()))
                .thenReturn(false);

        AddFriendException thrownException = assertThrows(AddFriendException.class, () -> {
           friendshipService.acceptFriendship("", httpSession);
        });
        assertEquals("No friend request available", thrownException.getMessage());
    }

    @Test
    public void testAcceptFriendshipRequestAlreadyDeclined(){
        when(httpSession.getAttribute(any())).thenReturn("not null");
        when(userRepository.findUserByUsername(any())).thenReturn(user);
        when(friendshipRepository.existsFriendshipByRequesterIdAndRecipientIdAndPendingIsTrue(any(), any()))
                .thenReturn(true);
        //friend request already declined
        when(friendshipRepository.existsFriendshipByRequesterIdAndRecipientIdAndDeclinedIsTrue(any(), any()))
                .thenReturn(true);

        AddFriendException thrownException = assertThrows(AddFriendException.class, () -> {
            friendshipService.acceptFriendship("", httpSession);
        });
        assertEquals("Friendship request already declined!", thrownException.getMessage());
    }

    //!tests for friendshipService.declineFriendship()
    @Test
    public void testDeclineFriendshipPass(){
      when(httpSession.getAttribute(any())).thenReturn("not null");
      when(userRepository.findUserByUsername(any())).thenReturn(user);
      when(friendshipRepository.existsFriendshipByRequesterIdAndRecipientIdAndPendingIsTrue(any(), any()))
              .thenReturn(true);
      when(friendshipRepository.existsFriendshipByRequesterIdAndRecipientIdAndDeclinedIsTrue(any(), any()))
              .thenReturn(false);
      when(friendshipRepository.existsFriendshipByRequesterIdAndRecipientIdAndPendingIsFalseAndDeclinedIsFalse(any(), any()))
              .thenReturn(false);

        ResponseEntity<String> responseEntity = friendshipService.declineFriendship("", httpSession);
        assertEquals("Friend request declined!", responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testDeclineFriendshipNoFriendRequestAvailable(){
        when(httpSession.getAttribute(any())).thenReturn("not null");
        when(userRepository.findUserByUsername(any())).thenReturn(user);
        when(friendshipRepository.existsFriendshipByRequesterIdAndRecipientIdAndPendingIsTrue(any(), any()))
                .thenReturn(false);

        AddFriendException thrownException = assertThrows(AddFriendException.class, () -> {
            friendshipService.declineFriendship("", httpSession);
        });
        assertEquals("You do not have a request to decline", thrownException.getMessage());
    }
}

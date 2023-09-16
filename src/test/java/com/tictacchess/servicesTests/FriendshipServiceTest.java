package com.tictacchess.servicesTests;

import com.tictacchess.exceptions.DatabaseException;
import com.tictacchess.exceptions.FriendshipAlreadyExistsException;
import com.tictacchess.exceptions.UserNotFoundException;
import com.tictacchess.model.Friendship;
import com.tictacchess.model.User;
import com.tictacchess.repository.FriendshipRepository;
import com.tictacchess.repository.UserRepository;
import com.tictacchess.services.FriendshipService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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


    //tests for friendshipService.addFriend()
    @Test
    public void testAddFriendPassed(){
        HttpSession httpSession = mock(HttpSession.class);
        User user = mock(User.class);

        when(friendshipRepository.existsFriendshipByRecipientIdAndRequesterId(any(), anyInt())).thenReturn(false);
        when(httpSession.getAttribute(any())).thenReturn("not null");
        when(userRepository.findUserByUsername(any())).thenReturn(user);
        when(friendshipRepository.save(any())).thenReturn(null);

        assertEquals("Friend request sent!", friendshipService.addFriend("", httpSession).getBody());
        verify(friendshipRepository, times(1)).save(any(Friendship.class));
    }

    @Test
    public void testAddFriendUserNotLoggedIn(){
        HttpSession httpSession = mock(HttpSession.class);

        when(httpSession.getAttribute(any())).thenReturn(null);

        UserNotFoundException thrownException = assertThrows(UserNotFoundException.class, () -> {
           friendshipService.addFriend("", httpSession);
        });
        assertEquals("Please log in before sending friend requests!", thrownException.getMessage());
        verify(httpSession, times(1)).getAttribute(any());
    }

    @Test
    public void testAddFriendUserNotFoundInDatabase(){
        HttpSession httpSession = mock(HttpSession.class);

        when(userRepository.findUserByUsername(any())).thenReturn(null);
        when(httpSession.getAttribute("username")).thenReturn("not null");

        UserNotFoundException thrownException = assertThrows(UserNotFoundException.class, () -> {
            friendshipService.addFriend("", httpSession);
        });
        assertEquals("Something is wrong with the username/usernames", thrownException.getMessage());
    }

    @Test
    public void testAddFriendFriendshipExists(){
        HttpSession httpSession = mock(HttpSession.class);
        User user = mock(User.class);

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
        HttpSession httpSession = mock(HttpSession.class);
        User user = mock(User.class);

        when(friendshipRepository.existsFriendshipByRecipientIdAndRequesterId(any(), any())).thenReturn(false);
        when(httpSession.getAttribute(any())).thenReturn("not null");
        when(userRepository.findUserByUsername(any())).thenReturn(user);
        doThrow(new RuntimeException()).when(friendshipRepository).save(any());

        DatabaseException thrownException = assertThrows(DatabaseException.class, () -> {
           friendshipService.addFriend("", httpSession);
        });
        assertEquals("A database error occurred while trying to save the friendship request.", thrownException.getMessage());
    }

}

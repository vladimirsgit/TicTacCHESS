package com.tictacchess.services;


import com.tictacchess.model.Friendship;
import com.tictacchess.model.User;
import com.tictacchess.repository.FriendshipRepository;
import com.tictacchess.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ScheduledMethodsService {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    public ScheduledMethodsService(UserRepository userRepository, FriendshipRepository friendshipRepository){
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    //if the user has not confirmed his email after 20 minutes, it means we can delete him from our database
    @Scheduled(fixedRate = 1200000)
    public void checkAndDeleteLateUsers(){
        ArrayList<User> unconfirmedUsers = userRepository.findUsersByConfirmedEmailFalse();
        for (User user :
                unconfirmedUsers) {
            long currentTime = System.currentTimeMillis();
            if(currentTime - user.getCreated_at().getTime() > 1200000){
                userRepository.delete(user);
            }
        }
    }

    @Scheduled(fixedRate = 86400000)
    public void checkAndDeleteFriendshipData(){
        ArrayList<Friendship> declinedFriendships = friendshipRepository.findFriendshipsByDeclinedTrue();

        for(Friendship friendship:
                declinedFriendships){
            long currentTime = System.currentTimeMillis();
            if(currentTime - friendship.getDeclined_at().getTime() > 86400000){
                friendshipRepository.delete(friendship);
            }
        }
    }
}

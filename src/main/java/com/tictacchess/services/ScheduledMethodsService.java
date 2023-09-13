package com.tictacchess.services;


import com.tictacchess.model.User;
import com.tictacchess.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ScheduledMethodsService {

    private final UserRepository userRepository;

    public ScheduledMethodsService(UserRepository userRepository){
        this.userRepository = userRepository;
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
}

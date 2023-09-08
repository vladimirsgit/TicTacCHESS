package com.tictacchess.backend.services;


import com.tictacchess.backend.model.User;
import com.tictacchess.backend.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sound.midi.Soundbank;
import java.sql.Timestamp;
import java.util.ArrayList;

@Component
public class ScheduledMethodsService {

    private final UserRepository userRepository;

    public ScheduledMethodsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRate = 1200000)
    public void checkAndDeleteLateUsers(){
        ArrayList<User> unconfirmedUsers = userRepository.findUsersByConfirmedEmailFalse();
        for (User user :
                unconfirmedUsers) {
            long currentTime = System.currentTimeMillis();
            if(user.getCreated_at().getTime() - currentTime > 1200000){
                userRepository.delete(user);
            }
        }
    }
}

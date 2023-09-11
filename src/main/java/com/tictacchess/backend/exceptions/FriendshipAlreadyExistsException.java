package com.tictacchess.backend.exceptions;

public class FriendshipAlreadyExistsException extends RuntimeException{
    public FriendshipAlreadyExistsException(String message){
        super(message);
    }
}

package com.tictacchess.backend.exceptions;

public class AuthDataInvalid extends RuntimeException{
    public AuthDataInvalid(String message){
        super(message);
    }
}

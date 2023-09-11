package com.tictacchess.backend.exceptions;

public class EmailException extends RuntimeException{
    public EmailException(String message){
        super(message);
    }
}

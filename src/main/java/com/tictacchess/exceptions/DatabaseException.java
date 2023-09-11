package com.tictacchess.exceptions;

public class DatabaseException extends RuntimeException{
    public DatabaseException(String message){
        super(message);
    }
}

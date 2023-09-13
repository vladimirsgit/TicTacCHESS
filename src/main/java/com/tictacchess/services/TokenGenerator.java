package com.tictacchess.services;

import java.security.SecureRandom;

public class TokenGenerator {

    public static String generateSecureToken(int length){
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder alphaNumString = new StringBuilder();
        StringBuilder token = new StringBuilder();

        int[][] intervalsVector = {{48, 57}, {65, 90}, {97, 122}};
        //generating token for email confirmation
        //we loop through the 3 arrays and then for each on we loop from the first element to the second
        //they represent numbers, capital letters and lower letters
        for(int i = 0; i < intervalsVector.length; i++){
            for(int j = intervalsVector[i][0]; j <= intervalsVector[i][1]; j++){
                alphaNumString.append((char)j);
            }
        }
        //after we created the String with possible values, we choose a char at random from it until we create a token
        for(int i = 0; i < alphaNumString.length(); i++){
            token.append(alphaNumString.charAt(secureRandom.nextInt(alphaNumString.length() - 1)));
        }
        return token.toString();
    }
}

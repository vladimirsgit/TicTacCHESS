package com.tictacchess.TestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestUtils {
    public static ObjectNode createRequestBodyJson(String lastname, String firstname, String email, String username, String password, String confirmPassword){
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode requestBodyJson = objectMapper.createObjectNode();
        requestBodyJson.put("last_name", lastname);
        requestBodyJson.put("first_name", firstname);
        requestBodyJson.put("email", email);
        requestBodyJson.put("username", username);
        requestBodyJson.put("password", password);
        requestBodyJson.put("confirmPassword", confirmPassword);

        return requestBodyJson;
    }
}

package pl.devims.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

public class AuthTokenUtils {

    public static String getLogin(String authToken) {
        if (authToken == null) {
            return null;
        }
        String payload = authToken.split("\\.")[1];

        byte[] decodedPayload = Base64.getUrlDecoder().decode(payload);
        ObjectMapper om = new ObjectMapper();
        TypeReference<Map<String,Object>> tr = new TypeReference<>() {};

        try {
            return om.readValue(decodedPayload, tr).get("login").toString();

        } catch (IOException ioe) {
            return null;
        }
    }
}

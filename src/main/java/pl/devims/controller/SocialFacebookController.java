package pl.devims.controller;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.web.bind.annotation.*;
import pl.devims.entity.Token;
import pl.devims.service.TokenService;
import pl.devims.service.UserService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@AllArgsConstructor
public class SocialFacebookController {

    private TokenService tokenService;
    private UserService userService;

    private final FacebookConnectionFactory facebookFactory = new FacebookConnectionFactory("4121717281172637",
            "d89dab8d60168284ae39d9842b938599");

    private final GoogleConnectionFactory googleFactory = new GoogleConnectionFactory("234520155978-8nv1ss78g63tmai5sv040imgjs6t9718.apps.googleusercontent.com",
            "jK2eVike25NiCuKOJEVMpWUs");

    @GetMapping(value = "/generateFacebookLoginUrl")
    public String fbProducer() {

        OAuth2Operations operations = facebookFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();

        params.setRedirectUri("http://localhost:4200/login/oauth/facebook");
        params.setScope("email,public_profile");

        String url = operations.buildAuthenticateUrl(params);
        Gson gson = new Gson();

        return gson.toJson(url);

    }
    @GetMapping(value = "/generateGoogleLoginUrl")
    public String googleProducer() {

        OAuth2Operations operations = googleFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();

        params.setRedirectUri("http://localhost:4200/login/oauth/google");
        params.setScope("email,public_profile");

        String url = operations.buildAuthenticateUrl(params);
        Gson gson = new Gson();

        return gson.toJson(url);

    }

    @PostMapping(value = "/ouath/facebook")
    public Token getToken(@RequestBody String authorizationCode) {
        OAuth2Operations operations = facebookFactory.getOAuthOperations();
        AccessGrant accessToken = operations.exchangeForAccess(authorizationCode, "http://localhost:4200/login/oauth/facebook",
                null);

        Connection<Facebook> connection = facebookFactory.createConnection(accessToken);
        Facebook facebook = connection.getApi();
        String[] fields = { "id", "email", "first_name", "last_name" };
        User userProfile = facebook.fetchObject("me", User.class, fields);
        userService.updateUser(userProfile);
        return tokenService.generateNewToken();

    }

}
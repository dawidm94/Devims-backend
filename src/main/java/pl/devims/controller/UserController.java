package pl.devims.controller;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.devims.dto.SocialUserDto;
import pl.devims.entity.User;
import pl.devims.service.UserService;

import javax.servlet.http.HttpServletRequest;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @PostMapping("/socialLogin")
    public boolean socialLogin(@RequestBody SocialUserDto socialUser, HttpServletRequest request) {
        boolean isLogged = userService.socialLogin(socialUser);
        return isLogged;
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        Gson gson = new Gson();
        return gson.toJson(userService.login(user));
    }

    @PostMapping("/register")
    public boolean register(@RequestBody User user) throws Exception {
        return userService.register(user);
    }

}

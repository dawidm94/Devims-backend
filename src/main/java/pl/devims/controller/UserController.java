package pl.devims.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.devims.entity.Token;
import pl.devims.entity.User;
import pl.devims.service.TokenService;

import javax.servlet.http.HttpServletRequest;

@RestController
@AllArgsConstructor
public class UserController {

    private TokenService tokenService;

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        return "login - " + user.getEmail() + ", pw - " + user.getPassword();
    }
    @PostMapping("/testLogin")
    public String loginNonAuth(@RequestBody User user, HttpServletRequest request) {
        Token token = tokenService.generateNewToken();
        request.getSession().setAttribute("token", token.getValue());
        return "NON AUTH login - " + user.getEmail() + ", pw - " + user.getPassword();
    }

}

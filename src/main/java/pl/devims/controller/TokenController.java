package pl.devims.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.devims.entity.Token;
import pl.devims.entity.User;
import pl.devims.service.TokenService;

import javax.servlet.http.HttpServletRequest;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TokenController {

    private TokenService tokenService;

    @GetMapping("/isTokenValid")
    public boolean login(@RequestParam("token") String token) {
        return tokenService.checkIfTokenIsValid(token);
    }

}

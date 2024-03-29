package pl.devims.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.devims.dto.DtoLoginCredentials;

@RestController
@RequestMapping("/login")
public class LoginController {

    @PostMapping()
    public ResponseEntity<Void> login(@RequestBody DtoLoginCredentials credentials) {
        return ResponseEntity.ok().build();
    }
}

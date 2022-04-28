package pl.devims.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @Value("spring.datasource.url")
    String dataSourceUrl;

    @Value("spring.datasource.username")
    String username;

    @Value("spring.datasource.password")
    String pw;

    @GetMapping()
    public ResponseEntity<String> getVersion() {
        return ResponseEntity.ok("I am alive!!, url: [" + dataSourceUrl + "], username: [" + username + "], password: [" + pw + "].");
    }
}

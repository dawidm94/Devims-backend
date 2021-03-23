package pl.devims.service;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.devims.dao.TokenDao;
import pl.devims.entity.Token;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@NoArgsConstructor
@Log4j2
public class TokenService {

    TokenDao tokenDao;

    private static final SecureRandom secureRandom = new SecureRandom();

    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    @Value("${tokenService.sessionExpiryTime}")
    private Long sessionExpiryTime;

    @Autowired
    public TokenService(TokenDao tokenDao) {
        this.tokenDao = tokenDao;
    }

    public void updateToken(Token token) {
        LocalDateTime now = LocalDateTime.now();
        token.setExpireDate(now.plusMinutes(sessionExpiryTime));
        tokenDao.save(token);
    }

    public boolean checkIfTokenIsValid(String tokenValue) {
        Token token = tokenDao.findByValue(tokenValue);
        LocalDateTime now = LocalDateTime.now();
        if (token != null && now.isBefore(token.getExpireDate())) {
            updateToken(token);
            return true;
        }
        log.warn("Token {} is not valid.", tokenValue);
        return false;
    }

    public Token generateNewToken() {
            byte[] randomBytes = new byte[24];
            secureRandom.nextBytes(randomBytes);
            LocalDateTime now = LocalDateTime.now();
            Token token = new Token(base64Encoder.encodeToString(randomBytes), now, now);
            updateToken(token);
            return token;
    }

    public Token saveNewToken(String tokenValue) {
        Token token = Token.builder()
                .createDate(LocalDateTime.now())
                .value(tokenValue)
                .build();
        updateToken(token);
        return token;
    }
}

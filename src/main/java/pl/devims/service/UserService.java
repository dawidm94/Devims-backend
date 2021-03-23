package pl.devims.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.devims.dao.UserDao;
import pl.devims.dto.SocialUserDto;
import pl.devims.entity.Token;
import pl.devims.entity.User;
import pl.devims.util.SocialMediaUtils;

@Service
@AllArgsConstructor
public class UserService {

    private UserDao userDao;
    private TokenService tokenService;
    private PasswordEncoder passwordEncoder;

    public boolean socialLogin(SocialUserDto socialUser) {
        if (!userDao.existsByEmailAndSocialServiceName(socialUser.getUserMail(), socialUser.getSocialServiceName())) {
            userDao.save(SocialMediaUtils.parseFromDtoToUser(socialUser));
        }
        tokenService.saveNewToken(socialUser.getToken());
        return true;
    }

    public String login(User user) {
        User dbUser = userDao.findByEmailAndSocialServiceNameIsNull(user.getEmail());
        if (passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
            Token token = tokenService.generateNewToken();
            return token.getValue();
        } else {
            return null;
        }
    }

    public boolean register(User user) throws Exception {
        if (userDao.existsByEmailAndSocialServiceNameIsNull(user.getEmail())){
            throw new Exception("Email istnieje w bazie");
        } else {
          String encodedPassword = passwordEncoder.encode(user.getPassword());
          user.setPassword(encodedPassword);
          userDao.save(user);
        }
        return true;
    }
}

package pl.devims.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.devims.entity.User;
import pl.devims.dao.UserDao;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    UserDao userDao;

    public void updateUser(org.springframework.social.facebook.api.User fbProfile) {
        User user = userDao.findByEmail(fbProfile.getEmail());

        if (user == null) {
            user = new User(fbProfile.getEmail(), fbProfile.getFirstName(), fbProfile.getLastName());
            userDao.save(user);
        }
    }
}

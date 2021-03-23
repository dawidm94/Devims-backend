package pl.devims.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.devims.entity.User;

public interface UserDao extends JpaRepository<User, Long> {

    User findByEmail(String email);
    boolean existsByEmailAndSocialServiceName(String email, String socialServiceName);
    boolean existsByEmailAndSocialServiceNameIsNull(String email);
    User findByEmailAndSocialServiceNameIsNull(String email);
}

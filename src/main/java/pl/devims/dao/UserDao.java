package pl.devims.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.devims.entity.User;

public interface UserDao extends JpaRepository<User, Long> {

    User findByEmail(String email);
}

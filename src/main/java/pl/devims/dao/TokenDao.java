package pl.devims.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.devims.entity.Token;

public interface TokenDao extends JpaRepository<Token, Long> {
    Token findByValue(String value);
}

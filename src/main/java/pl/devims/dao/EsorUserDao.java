package pl.devims.dao;

import org.springframework.data.repository.CrudRepository;
import pl.devims.entity.EsorUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EsorUserDao extends CrudRepository<EsorUser, Long> {
    Optional<EsorUser> findByLoginIgnoreCase(String login);
    List<EsorUser> findAllByLastSuccessLoginBetweenOrderByLastSuccessLoginDesc(LocalDateTime from, LocalDateTime to);
}

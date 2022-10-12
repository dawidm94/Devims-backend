package pl.devims.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.devims.entity.EsorEarnings;

import java.time.LocalDateTime;

public interface EsorEarningsDao extends JpaRepository<EsorEarnings, String> {

    void deleteAllByLastModifiedDateTimeBefore(LocalDateTime date);
}

package pl.devims.dao;

import org.springframework.data.repository.CrudRepository;
import pl.devims.entity.EsorMetric;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EsorMetricDao extends CrudRepository<EsorMetric, Long> {
    Optional<EsorMetric> findByLoginIgnoreCase(String login);
    List<EsorMetric> findAllByLastSuccessLoginBetween(LocalDateTime from, LocalDateTime to);
}

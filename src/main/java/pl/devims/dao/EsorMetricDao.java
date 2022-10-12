package pl.devims.dao;

import org.springframework.data.repository.CrudRepository;
import pl.devims.entity.EsorMetric;

import java.util.Optional;

public interface EsorMetricDao extends CrudRepository<EsorMetric, Long> {
    Optional<EsorMetric> findByLogin(String login);
}

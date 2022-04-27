package pl.devims.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.devims.entity.EsorMetric;

import java.util.Optional;

public interface EsorMetricDao extends JpaRepository<EsorMetric, Long> {
    Optional<EsorMetric> findByLogin(String login);
}

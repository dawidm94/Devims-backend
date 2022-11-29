package pl.devims.dao;

import org.springframework.data.repository.CrudRepository;
import pl.devims.entity.RequestHistory;

public interface RequestHistoryDao extends CrudRepository<RequestHistory, Long> {
}

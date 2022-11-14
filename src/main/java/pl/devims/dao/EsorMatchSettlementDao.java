package pl.devims.dao;

import org.springframework.data.repository.CrudRepository;
import pl.devims.entity.EsorMatchSettlement;
import pl.devims.entity.EsorUser;

import java.util.List;

public interface EsorMatchSettlementDao extends CrudRepository<EsorMatchSettlement, Long> {
    List<EsorMatchSettlement> findAllByUserAndSeasonId(EsorUser user, Long seasonId);
}

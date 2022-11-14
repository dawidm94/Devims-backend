package pl.devims.service;

import pl.devims.dto.DtoEsorSettlementWithMatch;
import pl.devims.entity.EsorMatchSettlement;

import java.io.IOException;
import java.util.List;

public interface EsorSettlementService {

    List<DtoEsorSettlementWithMatch> getSettlements(String authToken, Long seasonId) throws IOException;

    void updateSettlements(List<EsorMatchSettlement> settlements, String authToken);

}

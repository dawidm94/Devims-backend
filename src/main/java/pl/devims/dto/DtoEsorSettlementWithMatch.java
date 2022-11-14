package pl.devims.dto;

import lombok.Data;
import pl.devims.entity.EsorMatchSettlement;

@Data
public class DtoEsorSettlementWithMatch {

    private EsorMatchSettlement settlement;
    private DtoEsorMatch match;
}

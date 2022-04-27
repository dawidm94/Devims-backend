package pl.devims.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class DtoEsorSetPeriod {
    private List<Period> periods;
    private String seasonId;

    @Getter
    @Setter
    private static class Period {
        private String dateFrom;
        private String dateTo;
        private String reason;
    }
}

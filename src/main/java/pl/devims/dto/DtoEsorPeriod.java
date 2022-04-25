package pl.devims.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DtoEsorPeriod {
    private List<PeriodItem> items;
    private int gracePeriod;

    @Getter
    @Setter
    private static class PeriodItem {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime dateFrom;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime dateTo;

        private String reason;
    }
}

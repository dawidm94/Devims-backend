package pl.devims.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class DtoEsorUpcomingMatch {
    private List<MatchItem> items;

    @Getter
    @Setter
    private static class MatchItem {
        private long id;
        private String matchNumber;
        private String league;
        private String date;
        private String time;
        private String teamHome;
        private String teamVisitor;
    }
}

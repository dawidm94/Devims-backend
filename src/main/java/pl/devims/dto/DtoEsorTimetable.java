package pl.devims.dto;

import lombok.Data;

import java.util.List;

@Data
public class DtoEsorTimetable {
    private List<DtoEsorMatch> items;
    private Object pagination;
}

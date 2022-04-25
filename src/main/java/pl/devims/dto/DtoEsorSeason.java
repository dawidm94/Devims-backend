package pl.devims.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DtoEsorSeason {

    @JsonProperty("do")
    private String till;
    private String id;
    private String nazwa;
    private String od;
    private String pos;
    private String skrocona;
    private String visible;
}

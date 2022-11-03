package pl.devims.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DtoEsorInstances {
    private Long id;

    @JsonProperty("nazwa")
    private String name;
}

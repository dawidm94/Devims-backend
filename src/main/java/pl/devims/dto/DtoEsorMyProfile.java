package pl.devims.dto;

import lombok.Data;

@Data
public class DtoEsorMyProfile {

    private Long id;
    private String username;
    private Object permissions;
    private Object licenses;
}

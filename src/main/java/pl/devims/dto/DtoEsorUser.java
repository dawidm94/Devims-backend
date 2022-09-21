package pl.devims.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class DtoEsorUser {
    private String address;
    private String city;
    private String district;
    private String email;
    @JsonIgnore
    private String pesel;
    private String phone;
    private String photo;
    private String postalCode;
    private String voivodeship;
}

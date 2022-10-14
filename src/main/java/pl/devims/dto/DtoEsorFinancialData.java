package pl.devims.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class DtoEsorFinancialData {
    private String accountNumber;
    private String address;
    private String bankName;
    private String birthDate;
    private String birthPlace;
    private String city;
    private String nip;
    private String parentsNames;
    @JsonIgnore
    private String pesel;
    private String postalCode;
    private String taxOfficeName;
}

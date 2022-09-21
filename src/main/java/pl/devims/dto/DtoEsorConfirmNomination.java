package pl.devims.dto;

import lombok.Data;

import java.util.List;

@Data
public class DtoEsorConfirmNomination {

    private String arrival;
    private Integer buy;
    private Integer costAccommodation;
    private Double costPerKilometerGross;
    private String costTransport;
    private Integer costTravel;
    private String delegationNumber;
    private String departure;
    private Integer documentType;
    private Integer equivalent;
    private Long matchId;
    private Boolean privateTransport;
    private Boolean reservation;
    private List<Object> routes;
    private int routesDistanceKilometers;
    private int toPay;
    private String vehicleBrand;
    private String vehicleEngineSize;
    private String vehicleRegistrationNumber;
}

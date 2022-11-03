package pl.devims.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class DtoEsorNomination {

    private String arrival;
    private Integer buy;
    private Integer confirm;
    private Integer costAccommodation;
    private Integer costPerKilometerGross;
    private Integer costTransport;
    private Integer costTravel;
    private LocalDate date;
    private String delegationNumber;
    private String departure;
    private Integer distance;
    private Boolean distanceError;
    private String distanceErrorMessage;
    private String distanceGoogle;
    private Integer documentType;
    private Integer equivalent;
    private List<DtoEsorEquivalentOption> equivalentOptions;
    private Integer equivalentType;
    private Boolean hasDelegationNumber;
    private String league;
    private Long matchId;
    private String matchNumber;
    private List<Object> perKilometer;
    private Object priceList;
    private Boolean privateTransport;
    private Boolean reservation;
    private String round;
    private List<Object> routes;
    private String teamHome;
    private String teamVisitor;
    private String time;
    private BigDecimal toPay;
    private int update;
    private String vehicleBrand;
    private String vehicleEngineSize;
    private String vehicleRegistrationNumber;
}

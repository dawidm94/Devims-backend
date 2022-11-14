package pl.devims.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DtoEsorMatch {

    private List<Object> commisioneers;
    private String contact;
    private LocalDate date;
    private String hall;
    private String hallCerificate;
    private String hallMap;
    private String hotel;
    private String hotelMap;
    private String icalLink;
    private Long id;
    private String league;
    private boolean matchCommisioneer;
    private String matchNumber;
    private String paymentMethod;
    private List<Object> referees;
    private String remarks;
    private String round;
    private List<Object> tableReferees;
    private String teamHome;
    private String teamVisitor;
    private String time;
    private String toPay;
}

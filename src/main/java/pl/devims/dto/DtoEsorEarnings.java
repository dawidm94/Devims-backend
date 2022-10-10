package pl.devims.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DtoEsorEarnings {
    private double alreadyEarnedAmount;
    private double futureNominationsAmount;

    public void addAmount(DtoEsorNomination nomination) {
        if (LocalDate.now().isAfter(nomination.getDate())) {
            this.alreadyEarnedAmount += nomination.getToPay();

        } else {
            this.futureNominationsAmount += nomination.getToPay();
        }
    }
}

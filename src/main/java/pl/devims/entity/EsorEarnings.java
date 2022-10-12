package pl.devims.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import pl.devims.dto.DtoEsorNomination;
import pl.devims.model.ProcessStatus;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "esor_earnings")
@NoArgsConstructor
@Data
public class EsorEarnings {

    @Id
    private String uuid;
    private String status;
    private double alreadyEarnedAmount;
    private double futureNominationsAmount;

    @UpdateTimestamp
    private LocalDateTime lastModifiedDateTime;

    public void addAmount(DtoEsorNomination nomination) {
        if (LocalDate.now().isAfter(nomination.getDate())) {
            this.alreadyEarnedAmount += nomination.getToPay();

        } else {
            this.futureNominationsAmount += nomination.getToPay();
        }
    }

    public void setStatus(ProcessStatus status) {
        this.status = status.toString();
    }
}

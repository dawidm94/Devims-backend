package pl.devims.entity;

import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;
import pl.devims.dto.DtoEsorNomination;
import pl.devims.model.ProcessStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "esor_earnings")
@Data
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class EsorEarnings {

    @Id
    private String uuid;
    private String status;
    private double alreadyEarnedAmount;
    private double futureNominationsAmount;

    @Type( type = "json" )
    @Column(columnDefinition="TEXT")
    private Map<String, Double> teamPayments;

    @UpdateTimestamp
    private LocalDateTime lastModifiedDateTime;

    public EsorEarnings() {
        this.teamPayments = new HashMap<>();
    }

    public void addAmount(DtoEsorNomination nomination) {
        String teamHome = nomination.getTeamHome().trim();

        Double teamHomeSumAmount = this.teamPayments.get(teamHome) != null
                ? (this.teamPayments.get(teamHome) + nomination.getToPay())
                : nomination.getToPay();

        this.teamPayments.put(teamHome, teamHomeSumAmount);

        addToSum(nomination);
    }

    private void addToSum(DtoEsorNomination nomination) {
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

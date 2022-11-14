package pl.devims.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "esor_matches_settlements")
@NoArgsConstructor
@Data
public class EsorMatchSettlement {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id")
    private Long id;

    @ManyToOne()
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private EsorUser user;

    @Column(name = "season_id")
    private Long seasonId;

    @Column(name = "esor_match_id")
    private Long esorMatchId;

    @OneToOne
    private EsorCustomMatch customMatchId;

    @Column(name = "to_pay")
    private BigDecimal toPay;

    @Column(name = "is_paid")
    private boolean isPaid;

    @Column(name = "comment")
    private String comment;
}

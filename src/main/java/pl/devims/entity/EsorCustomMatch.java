package pl.devims.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "esor_custom_matches")
@NoArgsConstructor
@Data
public class EsorCustomMatch {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "date")
    private String date;

    @Column(name = "league")
    private String league;

    @Column(name = "teams", nullable = false)
    private String teams;
}

package pl.devims.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "esor_metrics")
@NoArgsConstructor
@Data
public class EsorMetric {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "login", nullable = false)
    private String login;

    @Column(name = "counter", nullable = false)
    private int counter;

    public EsorMetric(String login) {
        this.login = login;
        this.counter = 0;
    }
}

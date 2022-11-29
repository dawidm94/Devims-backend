package pl.devims.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Table(name = "request_history")
@NoArgsConstructor
@Entity
public class RequestHistory {

    @Id
    @SequenceGenerator(name = "seq_request_history", sequenceName = "seq_request_history", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_request_history")
    private Long id;

    @ManyToOne()
    @JoinColumn(name="user_id", referencedColumnName = "id")
    private EsorUser user;

    private String method;

    private String path;

    private LocalDateTime date;
}

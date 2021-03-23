package pl.devims.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String value;
    private LocalDateTime createDate;
    private LocalDateTime expireDate;

    public Token(String value, LocalDateTime createDate, LocalDateTime expireDate) {
        this.value = value;
        this.createDate = createDate;
        this.expireDate = expireDate;
    }
}

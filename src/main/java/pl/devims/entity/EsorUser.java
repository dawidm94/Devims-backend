package pl.devims.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "esor_users")
@NoArgsConstructor
@Data
public class EsorUser {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "login", nullable = false)
    private String login;

    @Column(name = "login_counter", nullable = false)
    private int loginCounter;

    @Column(name = "last_success_login")
    private LocalDateTime lastSuccessLogin;

    @Column(name = "last_failed_login")
    private LocalDateTime lastFailedLogin;

    public EsorUser(String login) {
        this.login = login;
        this.loginCounter = 0;
    }
}

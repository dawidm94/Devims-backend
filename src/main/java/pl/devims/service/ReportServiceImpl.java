package pl.devims.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.devims.dao.EsorMetricDao;
import pl.devims.entity.EsorMetric;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
public class ReportServiceImpl implements ReportService{

    private final EmailService emailService;
    private final EsorMetricDao esorMetricDao;

    @Override
    @Scheduled(cron = "10 0 0 * * *")
    public void sendDailyLoginReport() {
        String htmlMessage = prepareDailyReportMessage();
        emailService.sendMail("19sims94@gmail.com", "Dzienny raport logowań", htmlMessage, true);
    }

    private String prepareDailyReportMessage() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<EsorMetric> yesterdaysLogin = esorMetricDao.findAllByLastSuccessLoginBetween(yesterday.atStartOfDay(), LocalDate.now().atStartOfDay());

        StringBuilder sb = new StringBuilder();
        sb.append("<h2>Logowania dnia ").append(yesterday.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))).append(":</h2>");

        sb.append("""
                <table border="1">
                    <tr>
                        <th>Login</th>
                        <th>Licznik</th>
                        <th>Ostatnie logowanie</th>
                    </tr>
                """);

        yesterdaysLogin.forEach(singleLogin ->
            sb.append("<tr>")
                .append("<td align=\"center\">")
                    .append(singleLogin.getLogin())
                .append("</td>")
                .append("<td align=\"center\">")
                    .append(singleLogin.getCounter())
                .append("</td>")
                .append("<td align=\"center\">")
                    .append(singleLogin.getLastSuccessLogin().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .append("</td>")
            .append("</tr>")
        );

        sb.append("</table>");
        return sb.toString();
    }
}

package pl.devims.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.devims.dao.EsorEarningsDao;
import pl.devims.dto.DtoEsorNomination;
import pl.devims.entity.EsorEarnings;
import pl.devims.model.ProcessStatus;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Set;


@Service
@AllArgsConstructor
public class EsorEarningsAsyncServiceImpl implements EsorEarningsAsyncService {

    private RestTemplate restTemplate;
    private EsorEarningsDao esorEarningsDao;

    @Override
    @Async
    public void countEarningsAsync(EsorEarnings earnings, Set<Long> matchIds, String authToken) {

        try {
            matchIds.forEach(matchId -> {
                DtoEsorNomination nominationDetails = getNominationDetails(matchId, authToken);
                earnings.addAmount(nominationDetails);
            });
            earnings.setStatus(ProcessStatus.COMPLETED);

        } catch (Exception e) {
            earnings.setStatus(ProcessStatus.FAILED);
        }
        esorEarningsDao.save(earnings);
    }

    @Override
    @Scheduled(fixedDelay = 10 * 60 * 1000)
    @Transactional
    public void clearOldEarnings() {
        esorEarningsDao.deleteAllByLastModifiedDateTimeBefore(LocalDateTime.now().minusMinutes(2));
    }

    public DtoEsorNomination getNominationDetails(Long matchId, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);

        return restTemplate.exchange("https://sedzia.pzkosz.pl/api/nominations/" + matchId, HttpMethod.GET, new HttpEntity<>(headers), DtoEsorNomination.class).getBody();
    }
}

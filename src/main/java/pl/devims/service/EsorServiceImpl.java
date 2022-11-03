package pl.devims.service;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import pl.devims.dao.EsorEarningsDao;
import pl.devims.dao.EsorMetricDao;
import pl.devims.dto.*;
import pl.devims.entity.EsorEarnings;
import pl.devims.entity.EsorMetric;
import pl.devims.model.ProcessStatus;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EsorServiceImpl implements EsorService {
    private static final Logger log = LoggerFactory.getLogger(EsorServiceImpl.class);
    private final RestTemplate restTemplate;
    private final EsorMetricDao esorMetricDao;
    private final EsorEarningsAsyncService esorEarningsAsyncService;
    private final EsorEarningsDao esorEarningsDao;

    private final List<String> RC_LIST = List.of(
            "2 Liga Mężczyzn",
            "1 Liga Kobiet",
            "Suzuki 1 Liga Mężczyzn",
            "Energa Basket Liga Kobiet",
            "Energa Basket Liga"
    );
    private final long RC_INSTANCE_ID = 1L;

    private final int GATEWAY_TIMEOUT_MAX_RECALL_ATTEMPTS = 10;

    @Override
    public String getToken(DtoEsorCredentials esorCredentials) {
        String login = esorCredentials.getLogin().trim();

        try {
            String response = restTemplate.postForObject("https://sedzia.pzkosz.pl/api/login", esorCredentials, String.class);

            increaseEsorMetricCounter(login);

            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.getString("token");

        } catch (Exception e) {
            updateFailedLoginInEsorMetric(login);

            log.error("Login esor error.", e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    private void updateFailedLoginInEsorMetric(String login) {
        EsorMetric esorMetric = esorMetricDao.findByLoginIgnoreCase(login).orElse(new EsorMetric(login));
        esorMetric.setLastFailedLogin(LocalDateTime.now());
        esorMetricDao.save(esorMetric);
    }

    private void increaseEsorMetricCounter(String login) {
        EsorMetric esorMetric = esorMetricDao.findByLoginIgnoreCase(login).orElse(new EsorMetric(login));
        esorMetric.setCounter(esorMetric.getCounter() + 1);
        esorMetric.setLastSuccessLogin(LocalDateTime.now());

        esorMetricDao.save(esorMetric);
    }

    @Override
    public DtoEsorMyProfile getMyProfile(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);

            return restTemplate.exchange("https://sedzia.pzkosz.pl/api/me", HttpMethod.GET, new HttpEntity<>(headers), DtoEsorMyProfile.class).getBody();

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode());
        }
    }

    @Override
    public ResponseEntity<List<DtoEsorSeason>> getSeasons(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);

            return restTemplate.exchange("https://sedzia.pzkosz.pl/api/seasons", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {
            });

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode());
        }
    }

    @Override
    public DtoEsorSeason getCurrentSeason(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);

            return restTemplate.exchange("https://sedzia.pzkosz.pl/api/seasons/current", HttpMethod.GET, new HttpEntity<>(headers), DtoEsorSeason.class).getBody();

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode());
        }
    }

    @Override
    public DtoEsorPeriod getPeriodList(Long seasonId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);

            return restTemplate.exchange("https://sedzia.pzkosz.pl/api/periods?seasonId=" + seasonId, HttpMethod.GET, new HttpEntity<>(headers), DtoEsorPeriod.class).getBody();

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode());
        }
    }

    @Override
    public void setPeriods(DtoEsorSetPeriod esorSetPeriod, String authToken) throws InterruptedException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);

            restTemplate.exchange("https://sedzia.pzkosz.pl/api/periods", HttpMethod.POST, new HttpEntity<>(esorSetPeriod, headers), Void.class);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (HttpStatus.GATEWAY_TIMEOUT.equals(e.getStatusCode())) {
                int periodsSize = 0, attemptCounter = 0;
                while (periodsSize != esorSetPeriod.getPeriods().size() && attemptCounter < GATEWAY_TIMEOUT_MAX_RECALL_ATTEMPTS) {
                    TimeUnit.SECONDS.sleep(2);
                    attemptCounter++;

                    DtoEsorPeriod periods = getPeriodList(Long.parseLong(esorSetPeriod.getSeasonId()), authToken);
                    periodsSize = periods.getItems().size();
                }

                if (periodsSize == esorSetPeriod.getPeriods().size()) {
                    return;
                }
            }
            throw new ResponseStatusException(e.getStatusCode());
        }
    }

    @Override
    public DtoEsorUpcomingMatch getUpcomingMatch(Long seasonId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);

            return restTemplate.exchange("https://sedzia.pzkosz.pl/api/timetable/upcoming?seasonId=" + seasonId, HttpMethod.GET, new HttpEntity<>(headers), DtoEsorUpcomingMatch.class).getBody();

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode());
        }
    }

    @Override
    public ResponseEntity<byte[]> getDelegation(Long matchId, Long seasonId, String authToken) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<MultiValueMap<String, String>> entity = getFormUrlEncodedEntity(authToken);

        Long districtId = determineDistrictId(matchId, seasonId, authToken);

        return restTemplate.exchange("https://sedzia.pzkosz.pl/api/match/" + matchId + "/delegation/" + districtId,
            HttpMethod.POST,
            entity,
            byte[].class);
    }

    private Long determineDistrictId(Long matchId, Long seasonId, String authToken) throws Exception {
        List<DtoEsorInstances> instances = getInstances(seasonId, authToken);
        if (instances.size() == 1) {
            return instances.get(0).getId();
        }

        DtoEsorMatch match = getMatch(matchId, authToken).getBody();

        if ((match != null && match.getTableReferees().size() == 4)
                || (match != null && match.getLeague() != null && RC_LIST.contains(match.getLeague()))) {
            return RC_INSTANCE_ID;

        } else {
            return instances.stream()
                    .filter(instance -> RC_INSTANCE_ID != instance.getId())
                    .findFirst()
                    .orElseThrow(() -> new Exception("Couldn't determine instance."))
                    .getId();
        }
    }

    @Override
    public List<DtoEsorInstances> getInstances(Long seasonId, String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authToken);

        return restTemplate.exchange("https://sedzia.pzkosz.pl/api/instances?seasonId=" + seasonId,
                HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<List<DtoEsorInstances>>() {})
                .getBody();
    }

    @Override
    public ResponseEntity<byte[]> getMetric(Long matchId, String authToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<MultiValueMap<String, String>> entity = getFormUrlEncodedEntity(authToken);

        return restTemplate.exchange("https://sedzia.pzkosz.pl/api/match/" + matchId + "/metric",
            HttpMethod.POST,
            entity,
            byte[].class);
    }

    @Override
    public ResponseEntity<DtoEsorMatch> getMatch(Long matchId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);

            return restTemplate.exchange("https://sedzia.pzkosz.pl/api/match/" + matchId, HttpMethod.GET, new HttpEntity<>(headers), DtoEsorMatch.class);

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode());
        }
    }

    @Override
    public DtoEsorTimetable getTimetable(Long seasonId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);

            return restTemplate.exchange("https://sedzia.pzkosz.pl/api/timetable/my?seasonId=" + seasonId + "&page=1&perPage=1000", HttpMethod.GET, new HttpEntity<>(headers), DtoEsorTimetable.class).getBody();

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode());
        }
    }

    @Override
    public ResponseEntity<byte[]> getIcal(Long matchId, String authToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<MultiValueMap<String, String>> entity = getFormUrlEncodedEntity(authToken);

        ResponseEntity<byte[]> response = restTemplate.exchange("https://sedzia.pzkosz.pl/api/match/" + matchId + "/ical",
                HttpMethod.POST,
                entity,
                byte[].class);
        if (response.getBody() == null) {
            return ResponseEntity.notFound().build();
        }

        String icalText = new String(response.getBody());

        String endTime = getEndTimeFromIcal(icalText);

        String icalWithEndTime = icalText.substring(0, icalText.indexOf("DTSTAMP"))
                + (endTime != null ? ("DTEND:" + endTime + "\r\n") : "")
                + icalText.substring(icalText.indexOf("DTSTAMP"));

        return ResponseEntity.ok()
                .contentType(new MediaType("text", "calendar", StandardCharsets.UTF_8))
                .body(icalWithEndTime.getBytes());
    }

    @Override
    public ResponseEntity<List<DtoEsorBlanketNavigation>> getBlankets(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);

            return restTemplate.exchange("https://sedzia.pzkosz.pl/api/blankets", HttpMethod.GET, new HttpEntity<>(headers), new ParameterizedTypeReference<>() {
            });

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode());
        }
    }

    @Override
    public ResponseEntity<byte[]> getBlanket(Long blanketId, Long districtId, String authToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<MultiValueMap<String, String>> entity = getFormUrlEncodedEntity(authToken);

        return restTemplate.exchange("https://sedzia.pzkosz.pl/api/blankets/" + blanketId + "/" + districtId,
                HttpMethod.POST,
                entity,
                byte[].class);
    }

    @Override
    public int countNominations(Long seasonId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);

            String response = restTemplate.exchange("https://sedzia.pzkosz.pl/api/nominations/count?seasonId=" + seasonId, HttpMethod.GET, new HttpEntity<>(headers), String.class).getBody();

            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.getInt("count");

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode());
        }
    }

    @Override
    public DtoEsorTimetable getNominations(Long seasonId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);

            return restTemplate.exchange("https://sedzia.pzkosz.pl/api/nominations?seasonId=" + seasonId + "&page=1&perPage=1000", HttpMethod.GET, new HttpEntity<>(headers), DtoEsorTimetable.class).getBody();

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode());
        }
    }

    @Override
    public DtoEsorNomination getNominationDetails(Long matchId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);

            return restTemplate.exchange("https://sedzia.pzkosz.pl/api/nominations/" + matchId, HttpMethod.GET, new HttpEntity<>(headers), DtoEsorNomination.class).getBody();

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode());
        }
    }

    @Override
    public void rejectNomination(Long matchId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);

            ResponseEntity<String> response = restTemplate.exchange("https://sedzia.pzkosz.pl/api/nominations/" + matchId + "/reject", HttpMethod.POST, new HttpEntity<>(headers), String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ResponseStatusException(response.getStatusCode());
            }

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode());
        }
    }

    @Override
    public ResponseEntity<DtoEsorUser> getUser(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);

            return restTemplate.exchange("https://sedzia.pzkosz.pl/api/user", HttpMethod.GET, new HttpEntity<>(headers), DtoEsorUser.class);

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode());
        }
    }

    @Override
    public void confirmNotification(DtoEsorConfirmNomination nomination, Long matchId, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);

            restTemplate.exchange("https://sedzia.pzkosz.pl/api/nominations/" + matchId + "/confirm", HttpMethod.POST, new HttpEntity<>(nomination, headers), Void.class);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new ResponseStatusException(e.getStatusCode());
        }
    }

    @Override
    public ResponseEntity<EsorEarnings> calculateEarnings(Long seasonId, String authToken) {
        String uuid = UUID.randomUUID().toString();

        EsorEarnings earnings = new EsorEarnings();
        earnings.setUuid(uuid);
        earnings.setStatus(ProcessStatus.PENDING);

        esorEarningsDao.save(earnings);

        Set<Long> matchIds = getTimetable(seasonId, authToken).getItems()
                .stream()
                .map(DtoEsorMatch::getId)
                .collect(Collectors.toSet());

        esorEarningsAsyncService.countEarningsAsync(earnings, matchIds, authToken);

        return ResponseEntity.ok(earnings);
    }

    @Override
    public ResponseEntity<EsorEarnings> getEarnings(String uuid, String authToken) {
        Optional<EsorEarnings> earnings = esorEarningsDao.findById(uuid);

        return ResponseEntity.ok(earnings.orElse(new EsorEarnings()));
    }

    @Override
    public ResponseEntity<DtoEsorFinancialData> getFinancialData(String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);

            return restTemplate.exchange("https://sedzia.pzkosz.pl/api/user/financial-data", HttpMethod.GET, new HttpEntity<>(headers), DtoEsorFinancialData.class);

        } catch (HttpClientErrorException e) {
            throw new ResponseStatusException(e.getStatusCode());
        }
    }

    private String getEndTimeFromIcal(String icalText) {
        String pattern = "(DTSTART:\\d+T(\\d{2})\\d+Z)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(icalText);
        if (m.find()) {
            int matchDurationInHours = 2;
            String startTime = m.group(1);
            String startHour = m.group(2);
            String endHour = (Integer.parseInt(startHour) + matchDurationInHours) + "";

            return startTime.replaceAll("T" + startHour, "T" + endHour).substring(startTime.lastIndexOf(':') + 1, startTime.length());
        }
        return null;
    }

    private HttpEntity<MultiValueMap<String, String>> getFormUrlEncodedEntity(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("token", authToken);

        return new HttpEntity<>(map, headers);
    }
}

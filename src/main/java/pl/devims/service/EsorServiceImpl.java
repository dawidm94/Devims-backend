package pl.devims.service;

import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import pl.devims.dto.*;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class EsorServiceImpl implements EsorService {
    private static final Logger log = LoggerFactory.getLogger(EsorServiceImpl.class);
    private final RestTemplate restTemplate;

    @Override
    public String getToken(DtoEsorCredentials esorCredentials) {
        try {
            String response = restTemplate.postForObject("https://sedzia.pzkosz.pl/api/login", esorCredentials, String.class);
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.getString("token");

        } catch (Exception e) {
            log.error("Login esor error.", e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
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
    public void setPeriods(DtoEsorSetPeriod esorSetPeriod, String authToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);

            restTemplate.exchange("https://sedzia.pzkosz.pl/api/periods", HttpMethod.POST, new HttpEntity<>(esorSetPeriod, headers), Void.class);

        } catch (HttpClientErrorException | HttpServerErrorException e) {
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
    public ResponseEntity<byte[]> getDelegation(Long matchId, Long districtId, String authToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<MultiValueMap<String, String>> entity = getFormUrlEncodedEntity(authToken);

        return restTemplate.exchange("https://sedzia.pzkosz.pl/api/match/" + matchId + "/delegation/" + districtId,
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

package pl.devims.service;

import org.springframework.http.ResponseEntity;
import pl.devims.dto.*;

public interface EsorService {

    String getToken(DtoEsorCredentials esorCredentials);

    DtoEsorMyProfile getMyProfile(String authToken);

    DtoEsorSeason getCurrentSeason(String authToken);

    DtoEsorPeriod getPeriodList(Long seasonId, String authToken);

    void setPeriods(DtoEsorSetPeriod esorSetPeriod, String authToken);

    DtoEsorUpcomingMatch getUpcomingMatch(Long seasonId, String authToken);

    ResponseEntity<byte[]> getDelegation(Long matchId, Long districtId, String authToken);

    ResponseEntity<DtoEsorMatch> getMatch(Long matchId, String authToken);

    DtoEsorTimetable getTimetable(Long seasonId, String authToken);

    ResponseEntity<byte[]> getIcal(Long matchId, String authToken);
}

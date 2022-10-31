package pl.devims.service;

import org.springframework.http.ResponseEntity;
import pl.devims.dto.*;
import pl.devims.entity.EsorEarnings;

import java.util.List;

public interface EsorService {

    String getToken(DtoEsorCredentials esorCredentials);

    DtoEsorMyProfile getMyProfile(String authToken);

    ResponseEntity<List<DtoEsorSeason>> getSeasons(String authToken);

    DtoEsorSeason getCurrentSeason(String authToken);

    DtoEsorPeriod getPeriodList(Long seasonId, String authToken);

    void setPeriods(DtoEsorSetPeriod esorSetPeriod, String authToken) throws InterruptedException;

    DtoEsorUpcomingMatch getUpcomingMatch(Long seasonId, String authToken);

    ResponseEntity<byte[]> getDelegation(Long matchId, Long districtId, String authToken);

    ResponseEntity<byte[]> getMetric(Long matchId, String authToken);

    ResponseEntity<DtoEsorMatch> getMatch(Long matchId, String authToken);

    DtoEsorTimetable getTimetable(Long seasonId, String authToken);

    ResponseEntity<byte[]> getIcal(Long matchId, String authToken);

    ResponseEntity<List<DtoEsorBlanketNavigation>> getBlankets(String authToken);

    ResponseEntity<byte[]> getBlanket(Long blanketId, Long districtId, String authToken);

    int countNominations(Long seasonId, String authToken);

    DtoEsorTimetable getNominations(Long seasonId, String authToken);

    DtoEsorNomination getNominationDetails(Long matchId, String authToken);

    void rejectNomination(Long matchId, String authToken);

    ResponseEntity<DtoEsorUser> getUser(String authToken);

    void confirmNotification(DtoEsorConfirmNomination nomination, Long matchId, String authToken);

    ResponseEntity<EsorEarnings> calculateEarnings(Long seasonId, String authToken);

    ResponseEntity<EsorEarnings> getEarnings(String uuid, String authToken);

    ResponseEntity<DtoEsorFinancialData> getFinancialData(String authToken);

}

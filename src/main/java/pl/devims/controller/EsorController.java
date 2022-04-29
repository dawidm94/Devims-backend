package pl.devims.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.devims.dto.*;
import pl.devims.service.EsorService;

import java.util.List;

@RestController
@RequestMapping("/esor")
@CrossOrigin
public class EsorController {

    private final EsorService esorService;

    public EsorController(EsorService esorService) {
        this.esorService = esorService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody DtoEsorCredentials esorCredentials) {
        return ResponseEntity.ok(esorService.getToken(esorCredentials));
    }

    @GetMapping("/me")
    public ResponseEntity<DtoEsorMyProfile> getMyProfile(@RequestHeader(name="Esor-Token") String authToken) {
        return ResponseEntity.ok(esorService.getMyProfile(authToken));
    }

    @GetMapping("/seasons/current")
    public ResponseEntity<DtoEsorSeason> getCurrentSeason(@RequestHeader(name="Esor-Token") String authToken) {
        return ResponseEntity.ok(esorService.getCurrentSeason(authToken));
    }

    @GetMapping("/periods")
    public ResponseEntity<DtoEsorPeriod> getPeriodList(@RequestHeader(name="Esor-Token") String authToken, @RequestParam("seasonId") Long seasonId) {
        return ResponseEntity.ok(esorService.getPeriodList(seasonId, authToken));
    }

    @PostMapping("/periods")
    public ResponseEntity<Void> setPeriods(@RequestHeader(name="Esor-Token") String authToken, @RequestBody DtoEsorSetPeriod esorSetPeriod) {
        esorService.setPeriods(esorSetPeriod, authToken);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/timetable/upcoming")
    public ResponseEntity<DtoEsorUpcomingMatch> getUpcomingMatch(@RequestHeader(name="Esor-Token") String authToken, @RequestParam("seasonId") Long seasonId) {
        return ResponseEntity.ok(esorService.getUpcomingMatch(seasonId, authToken));
    }

    @GetMapping("/timetable/my")
    public ResponseEntity<DtoEsorTimetable> getTimetable(@RequestHeader(name="Esor-Token") String authToken, @RequestParam("seasonId") Long seasonId) {
        return ResponseEntity.ok(esorService.getTimetable(seasonId, authToken));
    }

    @GetMapping("/match/{matchId}/delegation/{districtId}")
    public ResponseEntity<byte[]> getDelegation(@RequestHeader(name="Esor-Token") String authToken, @PathVariable("matchId") Long matchId, @PathVariable("districtId") Long districtId) {
        return esorService.getDelegation(matchId, districtId, authToken);
    }

    @GetMapping("/match/{matchId}/metric")
    public ResponseEntity<byte[]> getMetric(@RequestHeader(name="Esor-Token") String authToken, @PathVariable("matchId") Long matchId) {
        return esorService.getMetric(matchId, authToken);
    }

    @GetMapping("/match/{matchId}/ical")
    public ResponseEntity<byte[]> getIcal(@RequestHeader(name="Esor-Token") String authToken, @PathVariable("matchId") Long matchId) {
        return esorService.getIcal(matchId, authToken);
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity<DtoEsorMatch> getMatch(@RequestHeader(name="Esor-Token") String authToken, @PathVariable("matchId") Long matchId) {
        return esorService.getMatch(matchId, authToken);
    }

    @GetMapping("/blankets")
    public ResponseEntity<List<DtoEsorBlanketNavigation>> getBlankets(@RequestHeader(name="Esor-Token") String authToken) {
        return esorService.getBlankets(authToken);
    }

    @GetMapping("/blankets/{blanketId}/{districtId}")
    public ResponseEntity<byte[]> getBlanket(@RequestHeader(name="Esor-Token") String authToken, @PathVariable("blanketId") Long blanketId, @PathVariable("districtId") Long districtId) {
        return esorService.getBlanket(blanketId, districtId, authToken);
    }

}

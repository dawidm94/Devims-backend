package pl.devims.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.devims.dto.DtoEsorSettlementWithMatch;
import pl.devims.entity.EsorMatchSettlement;
import pl.devims.service.EsorSettlementService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/esor/settlement")
@CrossOrigin
public class EsorSettlementController {

    private final EsorSettlementService settlementService;

    public EsorSettlementController(EsorSettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @GetMapping()
    public ResponseEntity<List<DtoEsorSettlementWithMatch>> getSettlements(@RequestParam("seasonId") Long seasonId, @RequestHeader(name="Esor-Token") String authToken) throws IOException {
        return ResponseEntity.ok(settlementService.getSettlements(authToken, seasonId));
    }

    @PutMapping()
    public ResponseEntity<Void> updateSettlements(@RequestBody List<EsorMatchSettlement> settlements, @RequestHeader(name="Esor-Token") String authToken) {
        settlementService.updateSettlements(settlements, authToken);
        return ResponseEntity.ok().build();
    }

//    @PostMapping("/periods")
//    public ResponseEntity<Void> setPeriods(@RequestHeader(name="Esor-Token") String authToken, @RequestBody DtoEsorSetPeriod esorSetPeriod) throws InterruptedException {
//        esorService.setPeriods(esorSetPeriod, authToken);
//        return ResponseEntity.ok().build();
//    }
}

package pl.devims.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pl.devims.dao.EsorMatchSettlementDao;
import pl.devims.dao.EsorUserDao;
import pl.devims.dto.DtoEsorMatch;
import pl.devims.dto.DtoEsorNomination;
import pl.devims.dto.DtoEsorSettlementWithMatch;
import pl.devims.dto.DtoEsorTimetable;
import pl.devims.entity.EsorMatchSettlement;
import pl.devims.entity.EsorUser;
import pl.devims.util.AuthTokenUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EsorSettlementServiceImpl implements EsorSettlementService {
//    private static final Logger log = LoggerFactory.getLogger(EsorSettlementServiceImpl.class);
    private final EsorUserDao esorUserDao;
    private final EsorMatchSettlementDao esorMatchSettlementDao;

    private final EsorService esorService;


    @Override
    public List<DtoEsorSettlementWithMatch> getSettlements(String authToken, Long seasonId) {
        String login = AuthTokenUtils.getLogin(authToken);
        EsorUser esorUser = esorUserDao.findByLoginIgnoreCase(login)
                .orElseThrow(() -> new RuntimeException("Cannot find login with login: " + login));

        List<EsorMatchSettlement> userSettlements = esorMatchSettlementDao.findAllByUserAndSeasonId(esorUser, seasonId);

        DtoEsorTimetable esorTimetable = esorService.getTimetable(seasonId, authToken);

        List<DtoEsorMatch> newMatches = filterNewMatches(userSettlements, esorTimetable.getItems());

        if (!CollectionUtils.isEmpty(newMatches)) {
            List<EsorMatchSettlement> savedNewSettlements = saveSettlement(newMatches, esorUser, seasonId, authToken);

            userSettlements.addAll(savedNewSettlements);
        }

        return packSettlementsWithMatch(userSettlements, esorTimetable);
    }

    @Override
    public void updateSettlements(List<EsorMatchSettlement> settlements, String authToken) {
        esorMatchSettlementDao.saveAll(settlements);
    }

    private List<DtoEsorSettlementWithMatch> packSettlementsWithMatch(List<EsorMatchSettlement> settlements, DtoEsorTimetable esorTimetable) {
        List<DtoEsorSettlementWithMatch> settlementWithMatchList = new ArrayList<>();
        Map<Long, DtoEsorMatch> matchIdToEsorMatchMap = esorTimetable.getItems()
                .stream()
                .collect(Collectors.toMap(DtoEsorMatch::getId, Function.identity()));

        settlements.forEach(settlement -> {
            DtoEsorSettlementWithMatch settlementWithMatch = new DtoEsorSettlementWithMatch();

            DtoEsorMatch dtoEsorMatch = matchIdToEsorMatchMap.get(settlement.getEsorMatchId());

            settlementWithMatch.setSettlement(settlement);
            settlementWithMatch.setMatch(dtoEsorMatch);

            settlementWithMatchList.add(settlementWithMatch);
        });

        settlementWithMatchList.sort(Comparator.comparing(x -> x.getMatch().getDate()));

        return settlementWithMatchList;
    }

    private List<EsorMatchSettlement> saveSettlement(List<DtoEsorMatch> newMatches, EsorUser user, Long seasonId, String authToken) {
        if (CollectionUtils.isEmpty(newMatches)) {
            return Collections.emptyList();
        }

        List<EsorMatchSettlement> newSettlements = new ArrayList<>();

        newMatches.forEach(match -> {
            DtoEsorNomination nominationDetails = esorService.getNominationDetails(match.getId(), authToken);
            EsorMatchSettlement settlement = new EsorMatchSettlement();
            settlement.setPaid(false);
            settlement.setUser(user);
            settlement.setEsorMatchId(match.getId());
            settlement.setSeasonId(seasonId);
            settlement.setToPay(nominationDetails.getToPay());

            newSettlements.add(settlement);
        });

        esorMatchSettlementDao.saveAll(newSettlements);

        return newSettlements;
    }

    private List<DtoEsorMatch> filterNewMatches(List<EsorMatchSettlement> savedMatchSettlements, List<DtoEsorMatch> actualEsorMatchList) {
        if (CollectionUtils.isEmpty(actualEsorMatchList)) {
            return Collections.emptyList();
        }

        Set<Long> savedEsorMatchIds = savedMatchSettlements.stream()
                .map(EsorMatchSettlement::getEsorMatchId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return actualEsorMatchList
                .stream()
                .filter(match -> !savedEsorMatchIds.contains(match.getId()))
                .filter(match -> LocalDate.now().isAfter(match.getDate()))
                .collect(Collectors.toList());
    }
}

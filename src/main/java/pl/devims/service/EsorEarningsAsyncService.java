package pl.devims.service;

import pl.devims.entity.EsorEarnings;

import java.util.Set;

public interface EsorEarningsAsyncService {
    void countEarningsAsync(EsorEarnings earnings, Set<Long> matchIds, String authToken);

    void clearOldEarnings();
}

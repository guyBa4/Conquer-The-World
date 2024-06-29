package Application.DataAccessLayer.Repositories;

import Application.Entities.users.PlayerStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface PlayerStatisticRepository extends JpaRepository<PlayerStatistic, UUID>{
    List<PlayerStatistic> findByRunningGameInstanceRunningIdAndMobilePlayerId(UUID runningGameId, UUID userId);
    List<PlayerStatistic> findByRunningGameInstanceRunningId(UUID runningGameId);
}


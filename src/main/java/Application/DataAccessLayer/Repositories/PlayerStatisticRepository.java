package Application.DataAccessLayer.Repositories;

import Application.Entities.users.PlayerStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface PlayerStatisticRepository extends JpaRepository<PlayerStatistics, UUID>{
    List<PlayerStatistics> findByRunningGameInstanceRunningIdAndMobilePlayerId(UUID runningGameId, UUID userId);
    List<PlayerStatistics> findByRunningGameInstanceRunningId(UUID runningGameId);
}


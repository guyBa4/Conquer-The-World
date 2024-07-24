package Application.DataAccessLayer.Repositories;

import Application.Entities.games.RunningGameInstance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RunningGameInstanceRepository extends JpaRepository<RunningGameInstance, UUID> {
    List<RunningGameInstance> findByGameInstance_Id(UUID gameInstanceId);
    List<RunningGameInstance> findByRunningIdAndMobilePlayers_id(UUID gameId, UUID userId);
    List<RunningGameInstance> findByCode(String code);
    
    @Query(nativeQuery = true,
            value = "SELECT * " +
                    "FROM running_game_instances r " +
                    "WHERE r.game_instance_id IN " +
                    "(SELECT id " +
                    "FROM game_instances " +
                    "WHERE CAST(game_instances.user_id AS TEXT) = :user_id );")
    Page<RunningGameInstance> findAllFiltered(@Param("user_id") String userId, Pageable pageable);
}

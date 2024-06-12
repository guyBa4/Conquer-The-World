package Application.Repositories;

import Application.Entities.Questionnaire;
import Application.Entities.RunningGameInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RunningGameInstanceRepository extends JpaRepository<RunningGameInstance, UUID> {
    List<RunningGameInstance> findByGameInstance_Id(UUID gameInstanceId);
    List<RunningGameInstance> findByRunningIdAndMobilePlayers_id(UUID gameId, UUID userId);
    List<RunningGameInstance> findByCode(String code);
}

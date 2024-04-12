package Application.Repositories;

import Application.Entities.Questionnaire;
import Application.Entities.RunningGameInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RunningGameInstanceRepository extends JpaRepository<RunningGameInstance, UUID> {

    RunningGameInstance findByRunningIdAndMobilePlayers_uuid(UUID gameId, UUID userId);
}

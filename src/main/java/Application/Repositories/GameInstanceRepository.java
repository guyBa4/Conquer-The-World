package Application.Repositories;

import Application.Entities.games.GameInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface GameInstanceRepository extends JpaRepository<GameInstance, UUID> {

    public List<GameInstance> findByQuestionnaireId(UUID questionnaireId);
}

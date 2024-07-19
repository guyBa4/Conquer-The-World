package Application.DataAccessLayer.Repositories;

import Application.Entities.games.GameInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface GameInstanceRepository extends JpaRepository<GameInstance, UUID> {

    public List<GameInstance> findByQuestionnaireId(UUID questionnaireId);
    
    @Query(nativeQuery = true,
           value = "SELECT DISTINCT * " +
                   "FROM game_instances gi " +
                   "WHERE gi.shared = true OR CAST(gi.user_id AS TEXT) = :user_id ;")
    public List<GameInstance> findAllFiltered(@Param("user_id") String userId);
}

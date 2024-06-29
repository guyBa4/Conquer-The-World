package Application.DataAccessLayer.Repositories;

import Application.Entities.games.GameStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GameStatisticRepository extends JpaRepository<GameStatistic, UUID> {

}

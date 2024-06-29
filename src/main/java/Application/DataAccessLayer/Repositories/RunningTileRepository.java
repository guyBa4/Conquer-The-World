package Application.DataAccessLayer.Repositories;

import Application.Entities.games.RunningTile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RunningTileRepository extends JpaRepository<RunningTile, UUID> {
}

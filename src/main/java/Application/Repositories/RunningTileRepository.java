package Application.Repositories;

import Application.Entities.games.RunningTile;
import com.oracle.truffle.api.library.ExportLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RunningTileRepository extends JpaRepository<RunningTile, UUID> {
}

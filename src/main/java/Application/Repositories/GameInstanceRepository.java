package Application.Repositories;

import Application.Entities.GameInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface GameInstanceRepository extends JpaRepository<GameInstance, UUID> {
}

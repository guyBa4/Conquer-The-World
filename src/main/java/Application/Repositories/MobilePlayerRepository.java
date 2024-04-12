package Application.Repositories;

import Application.Entities.Map;
import Application.Entities.MobilePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MobilePlayerRepository  extends JpaRepository<MobilePlayer, UUID> {
}
